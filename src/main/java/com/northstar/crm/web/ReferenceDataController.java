package com.northstar.crm.web;

import com.northstar.crm.repo.PlanRepository;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import java.nio.charset.StandardCharsets;import java.security.*;import java.util.*;
@RestController @RequestMapping("/reference") public class ReferenceDataController {
 private final PlanRepository plans;public ReferenceDataController(PlanRepository plans){this.plans=plans;}
 @GetMapping("/plans") public ResponseEntity<Map<String,Object>> plans(@RequestHeader(value="If-None-Match",required=false)String prior){var values=plans.findAll().stream().sorted(Comparator.comparing(com.northstar.crm.domain.PlanDefinition::getMonthlyPrice)).map(p->Map.<String,Object>of("name",p.getName(),"monthlyPrice",p.getMonthlyPrice().toPlainString(),"currency",p.getCurrency(),"apiRateLimit",p.getApiRateLimit(),"userLimit",p.getUserLimit())).toList();var body=Map.<String,Object>of("plans",values);var canonical=body.toString();var etag="\""+md5(canonical)+"\"";if(etag.equals(prior))return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(etag).build();return ResponseEntity.ok().eTag(etag).cacheControl(CacheControl.maxAge(java.time.Duration.ofMinutes(10))).body(body);}
 /** MD5 is intentionally non-cryptographic here: it is only an HTTP ETag for public reference data. */
 private String md5(String value){try{return HexFormat.of().formatHex(MessageDigest.getInstance("MD5").digest(value.getBytes(StandardCharsets.UTF_8)));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
}
