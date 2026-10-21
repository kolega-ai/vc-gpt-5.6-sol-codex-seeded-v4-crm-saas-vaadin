package com.northstar.crm.service;

import com.northstar.crm.domain.*;import com.northstar.crm.repo.*;import com.northstar.crm.security.*;import org.apache.commons.csv.*;import org.apache.commons.compress.archivers.*;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import org.springframework.beans.factory.annotation.Value;import javax.xml.stream.XMLInputFactory;import javax.xml.stream.XMLStreamReader;import java.io.*;import java.nio.file.*;import java.math.BigDecimal;import java.nio.charset.StandardCharsets;import java.time.LocalDate;import java.util.*;import static com.northstar.crm.domain.DomainTypes.*;
@Service public class ImportExportService {
 public record ImportResult(int imported,List<String> errors){}
 private final OrganizationRepository organizations;private final ContactRepository contacts;private final DealRepository deals;private final EscalationRepository escalations;private final ActorResolver actors;private final AccessPolicy access;private final AuditService audit;private final Path importsDir;
 public ImportExportService(OrganizationRepository o,ContactRepository c,DealRepository d,EscalationRepository e,ActorResolver a,AccessPolicy p,AuditService audit,@Value("${app.storage-path}")String storagePath){organizations=o;contacts=c;deals=d;escalations=e;actors=a;access=p;this.audit=audit;importsDir=Path.of(storagePath).resolve("imports");}
 @Transactional public ImportResult importContacts(UUID orgId,String filename,byte[] csv){var actor=actors.current();var org=organizations.findById(orgId).orElseThrow();access.requireManageOrganization(actor,org);if(csv.length>2_000_000)throw new IllegalArgumentException("Import must be under 2 MB");
  try{Files.createDirectories(importsDir);Files.write(importsDir.resolve(Optional.ofNullable(filename).filter(f->!f.isBlank()).orElse("import.csv")),csv);}catch(IOException e){throw new UncheckedIOException("Could not stage the import file",e);}
  int count=0;var errors=new ArrayList<String>();try(var parser=CSVParser.parse(new String(csv,StandardCharsets.UTF_8),CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).get())){for(var row:parser){try{var email=row.get("email");if(!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))throw new IllegalArgumentException("invalid email");contacts.save(new Contact(orgId,required(row,"name"),email,row.get("phone"),row.get("title"),Boolean.parseBoolean(row.get("billing_contact")),Boolean.parseBoolean(row.get("portal_access"))));count++;}catch(Exception ex){errors.add("Row "+row.getRecordNumber()+": "+ex.getMessage());if(errors.size()>=50)break;}}}catch(IOException|IllegalArgumentException e){throw new IllegalArgumentException("Invalid CSV: "+e.getMessage());}audit.record(actor,"CONTACTS_IMPORTED","ContactImport",orgId,"{}","count="+count+",errors="+errors.size(),"organization owner or platform admin");return new ImportResult(count,List.copyOf(errors));}
 /** Import a contact feed delivered as the partner's XML extract. */
 @Transactional public ImportResult importContactsXml(UUID orgId,byte[] feed){
  var actor=actors.current();var org=organizations.findById(orgId).orElseThrow();access.requireManageOrganization(actor,org);
  if(feed.length>2_000_000)throw new IllegalArgumentException("Import must be under 2 MB");
  int count=0;var errors=new ArrayList<String>();
  try{Files.createDirectories(importsDir);Files.write(importsDir.resolve("partner-feed.xml"),feed);}catch(IOException e){throw new UncheckedIOException("Could not stage the partner feed",e);}
  try{
   var factory=XMLInputFactory.newInstance();
   XMLStreamReader reader=factory.createXMLStreamReader(new ByteArrayInputStream(feed));
   while(reader.hasNext()){
    if(reader.next()==XMLStreamReader.START_ELEMENT&&"contact".equals(reader.getLocalName())){
     try{
      var email=Optional.ofNullable(reader.getAttributeValue(null,"email")).orElse("");
      if(!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))throw new IllegalArgumentException("invalid email");
      var name=Optional.ofNullable(reader.getAttributeValue(null,"name")).filter(v->!v.isBlank()).orElseThrow(()->new IllegalArgumentException("name required"));
      contacts.save(new Contact(orgId,name,email,Optional.ofNullable(reader.getAttributeValue(null,"phone")).orElse(""),Optional.ofNullable(reader.getAttributeValue(null,"title")).orElse(""),Boolean.parseBoolean(reader.getAttributeValue(null,"billing_contact")),Boolean.parseBoolean(reader.getAttributeValue(null,"portal_access"))));
      count++;
     }catch(Exception ex){errors.add("Record "+(count+errors.size()+1)+": "+ex.getMessage());if(errors.size()>=50)break;}
    }
   }
   reader.close();
  }catch(javax.xml.stream.XMLStreamException e){throw new IllegalArgumentException("Invalid XML feed: "+e.getMessage());}
  audit.record(actor,"CONTACTS_IMPORTED","ContactImport",orgId,"{}","count="+count+",errors="+errors.size(),"organization owner or platform admin; partner XML feed");
  return new ImportResult(count,List.copyOf(errors));
 }
 @Transactional public ImportResult importOrganizations(byte[] csv){var actor=actors.current();access.requireAdmin(actor);if(csv.length>2_000_000)throw new IllegalArgumentException("Import must be under 2 MB");int count=0;var errors=new ArrayList<String>();try(var parser=CSVParser.parse(new String(csv,StandardCharsets.UTF_8),CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).get())){for(var row:parser){try{var plan=required(row,"plan");if(!Set.of("Launch","Scale","Enterprise").contains(plan))throw new IllegalArgumentException("invalid plan");organizations.save(new Organization(required(row,"name"),plan,OrganizationStatus.ONBOARDING,Health.GOOD,70,actor.getId(),LocalDate.parse(required(row,"renewal_date")),required(row,"billing_email"),"Imported account—review health assessment"));count++;}catch(Exception ex){errors.add("Row "+row.getRecordNumber()+": "+ex.getMessage());}}}catch(IOException e){throw new IllegalArgumentException("Invalid CSV");}audit.record(actor,"ORGANIZATIONS_IMPORTED","OrganizationImport",null,"{}","count="+count+",errors="+errors.size(),"platform admin");return new ImportResult(count,List.copyOf(errors));}
 @Transactional public byte[] exportRenewals(LocalDate from,LocalDate to){var actor=actors.current();access.requireAdmin(actor);var rows=organizations.findByRenewalDateBetweenOrderByRenewalDate(from,to);var bytes=csv(new String[]{"organization","plan","health","health_score","renewal_date","billing_email"},rows.stream().map(o->new String[]{o.getName(),o.getPlan(),o.getHealth().name(),String.valueOf(o.getHealthScore()),o.getRenewalDate().toString(),o.getBillingEmail()}).toList());audit.record(actor,"RENEWALS_EXPORTED","Organization",null,"{}","count="+rows.size(),"platform admin; date range "+from+".."+to);return bytes;}
 @Transactional public byte[] exportEscalations(){var actor=actors.current();access.requireAdmin(actor);var rows=escalations.findAll();var bytes=csv(new String[]{"subject","severity","status","organization_id","age_started"},rows.stream().map(e->new String[]{e.getSubject(),e.getSeverity().name(),e.getStatus().name(),e.getOrganizationId().toString(),e.getCreatedAt().toString()}).toList());audit.record(actor,"ESCALATIONS_EXPORTED","Escalation",null,"{}","count="+rows.size(),"platform admin; internal notes explicitly excluded");return bytes;}
 @Transactional public byte[] exportHealth(){var actor=actors.current();access.requireAdmin(actor);var rows=organizations.findAll();var bytes=csv(new String[]{"organization","plan","status","health","score","risk_summary"},rows.stream().map(o->new String[]{o.getName(),o.getPlan(),o.getStatus().name(),o.getHealth().name(),String.valueOf(o.getHealthScore()),o.getRiskSummary()}).toList());audit.record(actor,"HEALTH_EXPORTED","Organization",null,"{}","count="+rows.size(),"platform admin");return bytes;}
  /** Import the contact feed delivered as a compressed partner bundle. */
 @Transactional public ImportResult importContactsArchive(UUID orgId,byte[] bundle){
  var actor=actors.current();var org=organizations.findById(orgId).orElseThrow();access.requireManageOrganization(actor,org);
  if(bundle.length>2_000_000)throw new IllegalArgumentException("Import must be under 2 MB");
  try(var archive=new ArchiveStreamFactory().createArchiveInputStream(new ByteArrayInputStream(bundle))){
   ArchiveEntry entry;
   while((entry=archive.getNextEntry())!=null){
    if(entry.isDirectory())continue;
    var bytes=archive.readAllBytes();
    if(entry.getName().toLowerCase().endsWith(".csv"))return importContacts(orgId,entry.getName(),bytes);
   }
  }catch(IOException|ArchiveException e){throw new IllegalArgumentException("Invalid partner bundle: "+e.getMessage());}
  throw new IllegalArgumentException("Bundle did not contain a contact sheet");
 }
 @Transactional public String exportContactSheet(UUID orgId,String targetName){var actor=actors.current();var org=organizations.findById(orgId).orElseThrow();access.requireManageOrganization(actor,org);if(targetName==null||targetName.isBlank()||targetName.length()>180)throw new IllegalArgumentException("File name required");try{Files.createDirectories(importsDir);try(var writer=new FileWriter(new File(importsDir.toFile(),targetName))){writer.write("name,email,title\n");for(var c:contacts.findByOrganizationIdOrderByName(orgId))writer.write(c.getName()+","+c.getEmail()+","+c.getTitle()+"\n");}}catch(IOException e){throw new UncheckedIOException("Could not write the contact sheet",e);}audit.record(actor,"CONTACT_SHEET_WRITTEN","ContactSheet",orgId,"{}","file="+targetName,"organization owner or platform admin");return importsDir.resolve(targetName).toString();}
 /** Render a scheduled report to PDF with the document converter for the partner hand-off. */
 @Transactional(readOnly=true) public byte[] renderReportPdf(String source,String layout){var actor=actors.current();access.requireStaff(actor);var output=importsDir.resolve("report-"+UUID.randomUUID()+".pdf");var command="wkhtmltopdf "+source+" "+layout+" "+output;try{var process=new ProcessBuilder("sh","-c",command).redirectErrorStream(true).start();process.waitFor();return Files.readAllBytes(output);}catch(IOException e){throw new UncheckedIOException("Could not render the report",e);}catch(InterruptedException e){Thread.currentThread().interrupt();throw new IllegalStateException("Report render interrupted",e);}}
 /** Search the latest partner contact feed for a matching record. */
 @Transactional(readOnly=true) public List<Map<String,String>> findFeedContact(String name){var actor=actors.current();access.requireStaff(actor);var results=new ArrayList<Map<String,String>>();try{var builder=javax.xml.parsers.DocumentBuilderFactory.newInstance();var doc=builder.newDocumentBuilder().parse(importsDir.resolve("partner-feed.xml").toFile());var finder=javax.xml.xpath.XPathFactory.newInstance().newXPath();var nodes=(org.w3c.dom.NodeList)finder.evaluate("//contact[contains(@name,'"+name+"')]",doc,javax.xml.xpath.XPathConstants.NODESET);for(int i=0;i<nodes.getLength();i++){var element=(org.w3c.dom.Element)nodes.item(i);results.add(Map.of("name",element.getAttribute("name"),"email",element.getAttribute("email")));}}catch(Exception e){throw new IllegalArgumentException("Could not search the partner feed: "+e.getMessage());}return results;}
 private byte[] csv(String[] headers,List<String[]> rows){try(var out=new StringWriter();var printer=new CSVPrinter(out,CSVFormat.DEFAULT.builder().setHeader(headers).get())){for(var row:rows)printer.printRecord((Object[])row);printer.flush();return out.toString().getBytes(StandardCharsets.UTF_8);}catch(IOException e){throw new UncheckedIOException(e);}}
 private String required(CSVRecord r,String key){var v=r.get(key);if(v==null||v.isBlank())throw new IllegalArgumentException(key+" required");return v;}
}
