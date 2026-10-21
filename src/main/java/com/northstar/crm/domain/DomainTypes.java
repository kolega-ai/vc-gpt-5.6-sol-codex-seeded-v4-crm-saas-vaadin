package com.northstar.crm.domain;

public final class DomainTypes {
    private DomainTypes() {}
    public enum Role { PLATFORM_ADMIN, ACCOUNT_MANAGER, SUPPORT_AGENT, CUSTOMER_CONTACT }
    public enum OrganizationStatus { ONBOARDING, ACTIVE, AT_RISK, SUSPENDED }
    public enum Health { EXCELLENT, GOOD, WATCH, CRITICAL }
    public enum DealStage { LEAD, QUALIFIED, PROPOSAL, NEGOTIATION, CLOSED_WON, CLOSED_LOST, RENEWAL_PENDING }
    public enum EscalationStatus { OPEN, WAITING_ON_CUSTOMER, WAITING_ON_INTERNAL, RESOLVED, CLOSED }
    public enum Severity { LOW, MEDIUM, HIGH, CRITICAL }
    public enum Visibility { INTERNAL, CUSTOMER_VISIBLE, BILLING_ONLY }
    public enum DocumentCategory { CONTRACT, SECURITY_QUESTIONNAIRE, INVOICE, ONBOARDING }
    public enum ApiKeyStatus { ACTIVE, REVOKED, EXPIRED }
    public enum WebhookEvent { ESCALATION_UPDATED, DEAL_STAGE_CHANGED, DOCUMENT_SHARED, RENEWAL_DUE }
}
