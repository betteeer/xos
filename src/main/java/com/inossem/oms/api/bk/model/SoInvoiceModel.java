package com.inossem.oms.api.bk.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.inossem.oms.api.bk.utils.BigDecimalSerialize;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author kgh
 * @date 2022-12-08 17:40
 */
public class SoInvoiceModel {

    public static class SoInvoiceItem {
        private String item_no;
        private String model;
        private String description;
        private BigDecimal qty;
        @JsonSerialize(using = BigDecimalSerialize.class)
        private BigDecimal unit_price;
        @JsonSerialize(using = BigDecimalSerialize.class)
        private BigDecimal total;
        //private String expenseAccountId;        //CoA ID   这个应该是BK里面根据OBYC去自动决定的
        //private String expenseAccount;        //CoA code
        //private String bankAccount;
        private String dr_cr;
        private Integer debit_coa_id;
        private String debit_coa_code;
        private String debit_coa_name;
        private Integer credit_coa_id;
        private String credit_coa_code;
        private String credit_coa_name;

        public String getItem_no() {
            return item_no;
        }

        public SoInvoiceItem setItem_no(String item_no) {
            this.item_no = item_no;
            return this;
        }
        public String getModel() {
            return model;
        }

        public SoInvoiceItem setModel(String model) {
            this.model = model;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public SoInvoiceItem setDescription(String description) {
            this.description = description;
            return this;
        }

        public BigDecimal getQty() {
            return qty;
        }

        public SoInvoiceItem setQty(BigDecimal qty) {
            this.qty = qty;
            return this;
        }



        public BigDecimal getUnit_price() {
            return unit_price;
        }

        public SoInvoiceItem setUnit_price(BigDecimal unit_price) {
            this.unit_price = unit_price;
            return this;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public SoInvoiceItem setTotal(BigDecimal total) {
            this.total = total.setScale(2, BigDecimal.ROUND_HALF_UP);
            //this.total = total;
            return this;
        }

        public String getDr_cr() {
            return dr_cr;
        }

        public Integer getDebit_coa_id() {
            return debit_coa_id;
        }

        public String getDebit_coa_code() {
            return debit_coa_code;
        }

        public String getDebit_coa_name() {
            return debit_coa_name;
        }

        public Integer getCredit_coa_id() {
            return credit_coa_id;
        }

        public String getCredit_coa_code() {
            return credit_coa_code;
        }

        public String getCredit_coa_name() {
            return credit_coa_name;
        }

        public SoInvoiceItem setDr_cr(String dr_cr) {
            this.dr_cr = dr_cr;
            return this;
        }

        public SoInvoiceItem setDebit_coa_id(Integer debit_coa_id) {
            this.debit_coa_id = debit_coa_id;
            return this;

        }

        public SoInvoiceItem setDebit_coa_code(String debit_coa_code) {
            this.debit_coa_code = debit_coa_code;
            return this;
        }

        public SoInvoiceItem setDebit_coa_name(String debit_coa_name) {
            this.debit_coa_name = debit_coa_name;
            return this;
        }

        public SoInvoiceItem setCredit_coa_id(Integer credit_coa_id) {
            this.credit_coa_id = credit_coa_id;
            return this;
        }

        public SoInvoiceItem setCredit_coa_code(String credit_coa_code) {
            this.credit_coa_code = credit_coa_code;
            return this;
        }

        public SoInvoiceItem setCredit_coa_name(String credit_coa_name) {
            this.credit_coa_name = credit_coa_name;
            return this;
        }
    }

    private String company_id;
    //private String supplierId;
    private String company_code;
    private String company_name;
    private String company_address;
    private String company_tel;
    private String company_email;
    //private String companyLogo;
    private String company_gst_no;
    private String company_pst_no;
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal gst;
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal pst;
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal qst;
    private String reference_no;
    private String invoice_currency;
    private String pay_method;
    private String invoice_create_date;
    private String invoice_due_date;
    private String posting_date;

    private String bill_to_customer_id; // "0030000018",				//收票方公司的客户ID
    private String bill_to_receiver; // "",
    private String bill_to_company; // "5599",						//收票方公司名称，   这是哪个号？
    private String bill_to_street; // "",
    private String bill_to_city; // "",
    private String bill_to_province; // "",
    private String bill_to_country; // "",
    private String bill_to_postal_code; // "",
    private String bill_to_tel; // "",
    private String bill_to_email; // "55@nt.ca",
    private String ship_to_receiver; // "",
    private String ship_to_company; // "5599",
    private String ship_to_street; // "",
    private String ship_to_city; // "",
    private String ship_to_province; // "",
    private String ship_to_country; // null,
    private String ship_to_postal_code; // "",
    private String ship_to_tel; // "",
    private String ship_to_email; // "55@nt.ca",


    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal net_amount;
    //private String shipping;
    //private String discount;
    /*@JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalTaxable;
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal tps;
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal tvq;
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal tvp;*/
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal total_tax;
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal total_fee;
    private BigDecimal total_fee_cad;
    private BigDecimal exchange_rate;
    //private String deposit;
    private String invoice_comments;
    //private Integer fileId;
    //private Integer filePageIndex;
    //private String fileUrl;
    private String br_type;
    private Integer creator;

    private Integer bank_id; // "",
    private String bank_account; // "",
    private String bank_name; // "",

    private List<SoInvoiceItem> items;

    private List tax_content;

    public String getCompany_id() {
        return company_id;
    }

    public SoInvoiceModel setCompany_id(String company_id) {
        this.company_id = company_id;
        return this;
    }

    public String getCompany_code() {
        return company_code;
    }

    public SoInvoiceModel setCompany_code(String company_code) {
        this.company_code = company_code;
        return this;
    }

    public String getCompany_name() {
        return company_name;
    }

    public SoInvoiceModel setCompany_name(String company_name) {
        this.company_name = company_name;
        return this;
    }

    public String getCompany_address() {
        return company_address;
    }

    public SoInvoiceModel setCompany_address(String company_address) {
        this.company_address = company_address;
        return this;
    }

    public String getCompany_tel() {
        return company_tel;
    }

    public SoInvoiceModel setCompany_tel(String company_tel) {
        this.company_tel = company_tel;
        return this;
    }

    public String getCompany_email() {
        return company_email;
    }

    public SoInvoiceModel setCompany_email(String company_email) {
        this.company_email = company_email;
        return this;
    }

    public String getCompany_gst_no() {
        return company_gst_no;
    }

    public SoInvoiceModel setCompany_gst_no(String company_gst_no) {
        this.company_gst_no = company_gst_no;
        return this;
    }

    public String getCompany_pst_no() {
        return company_pst_no;
    }

    public SoInvoiceModel setCompany_pst_no(String company_pst_no) {
        this.company_pst_no = company_pst_no;
        return this;
    }

    public BigDecimal getGst() {
        return gst;
    }

    public SoInvoiceModel setGst(BigDecimal gst) {
        this.gst = gst;
        return this;
    }

    public BigDecimal getPst() {
        return pst;
    }

    public SoInvoiceModel setPst(BigDecimal pst) {
        this.pst = pst;
        return this;
    }

    public BigDecimal getQst() {
        return qst;
    }

    public SoInvoiceModel setQst(BigDecimal qst) {
        this.qst = qst;
        return this;
    }

    public String getReference_no() {
        return reference_no;
    }

    public SoInvoiceModel setReference_no(String reference_no) {
        this.reference_no = reference_no;
        return this;
    }

    public String getInvoice_currency() {
        return invoice_currency;
    }

    public SoInvoiceModel setInvoice_currency(String invoice_currency) {
        this.invoice_currency = invoice_currency;
        return this;
    }

    public String getPay_method() {
        return pay_method;
    }

    public SoInvoiceModel setPay_method(String pay_method) {
        this.pay_method = pay_method;
        return this;
    }

    public String getInvoice_create_date() {
        return invoice_create_date;
    }

    public SoInvoiceModel setInvoice_create_date(String invoice_create_date) {
        this.invoice_create_date = invoice_create_date;
        return this;
    }

    public String getInvoice_due_date() {
        return invoice_due_date;
    }

    public SoInvoiceModel setInvoice_due_date(String invoice_due_date) {
        this.invoice_due_date = invoice_due_date;
        return this;
    }

    public String getPosting_date() {
        return posting_date;
    }

    public SoInvoiceModel setPosting_date(String posting_date) {
        this.posting_date = posting_date;
        return  this;
    }

    public String getBill_to_customer_id() {
        return bill_to_customer_id;
    }

    public SoInvoiceModel setBill_to_customer_id(String bill_to_customer_id) {
        this.bill_to_customer_id = bill_to_customer_id;
        return this;
    }

    public String getBill_to_receiver() {
        return bill_to_receiver;
    }

    public SoInvoiceModel setBill_to_receiver(String bill_to_receiver) {
        this.bill_to_receiver = bill_to_receiver;
        return this;
    }

    public String getBill_to_company() {
        return bill_to_company;
    }

    public SoInvoiceModel setBill_to_company(String bill_to_company) {
        this.bill_to_company = bill_to_company;
        return this;
    }

    public String getBill_to_street() {
        return bill_to_street;
    }

    public SoInvoiceModel setBill_to_street(String bill_to_street) {
        this.bill_to_street = bill_to_street;
        return this;
    }

    public String getBill_to_city() {
        return bill_to_city;
    }

    public SoInvoiceModel setBill_to_city(String bill_to_city) {
        this.bill_to_city = bill_to_city;
        return this;
    }

    public String getBill_to_province() {
        return bill_to_province;
    }

    public SoInvoiceModel setBill_to_province(String bill_to_province) {
        this.bill_to_province = bill_to_province;
        return this;
    }

    public String getBill_to_country() {
        return bill_to_country;
    }

    public SoInvoiceModel setBill_to_country(String bill_to_country) {
        this.bill_to_country = bill_to_country;
        return  this;

    }

    public String getBill_to_postal_code() {
        return bill_to_postal_code;
    }

    public SoInvoiceModel setBill_to_postal_code(String bill_to_postal_code) {
        this.bill_to_postal_code = bill_to_postal_code;
        return  this;
    }

    public String getBill_to_tel() {
        return bill_to_tel;
    }

    public SoInvoiceModel setBill_to_tel(String bill_to_tel) {
        this.bill_to_tel = bill_to_tel;
        return  this;
    }

    public String getBill_to_email() {
        return bill_to_email;
    }

    public SoInvoiceModel setBill_to_email(String bill_to_email) {
        this.bill_to_email = bill_to_email;
        return  this;
    }

    public String getShip_to_receiver() {
        return ship_to_receiver;
    }

    public SoInvoiceModel setShip_to_receiver(String ship_to_receiver) {
        this.ship_to_receiver = ship_to_receiver;
        return  this;
    }

    public String getShip_to_company() {
        return ship_to_company;
    }

    public SoInvoiceModel setShip_to_company(String ship_to_company) {
        this.ship_to_company = ship_to_company;
        return  this;
    }

    public String getShip_to_street() {
        return ship_to_street;
    }

    public SoInvoiceModel setShip_to_street(String ship_to_street) {
        this.ship_to_street = ship_to_street;
        return  this;
    }

    public String getShip_to_city() {
        return ship_to_city;
    }

    public SoInvoiceModel setShip_to_city(String ship_to_city) {
        this.ship_to_city = ship_to_city;
        return  this;
    }

    public String getShip_to_province() {
        return ship_to_province;
    }

    public SoInvoiceModel setShip_to_province(String ship_to_province) {
        this.ship_to_province = ship_to_province;
        return  this;
    }

    public String getShip_to_country() {
        return ship_to_country;
    }

    public SoInvoiceModel setShip_to_country(String ship_to_country) {
        this.ship_to_country = ship_to_country;
        return  this;
    }

    public String getShip_to_postal_code() {
        return ship_to_postal_code;
    }

    public SoInvoiceModel setShip_to_postal_code(String ship_to_postal_code) {
        this.ship_to_postal_code = ship_to_postal_code;
        return  this;
    }

    public String getShip_to_tel() {
        return ship_to_tel;
    }

    public SoInvoiceModel setShip_to_tel(String ship_to_tel) {
        this.ship_to_tel = ship_to_tel;
        return  this;
    }

    public String getShip_to_email() {
        return ship_to_email;
    }

    public SoInvoiceModel setShip_to_email(String ship_to_email) {
        this.ship_to_email = ship_to_email;
        return  this;
    }

    public BigDecimal getNet_amount() {
        return net_amount;
    }

    public SoInvoiceModel setNet_amount(BigDecimal net_amount) {
        this.net_amount = net_amount;
        return  this;
    }

    public BigDecimal getTotal_tax() {
        return total_tax;
    }

    public SoInvoiceModel setTotal_tax(BigDecimal total_tax) {
        this.total_tax = total_tax;
        return  this;
    }

    public BigDecimal getTotal_fee() {
        return total_fee;
    }

    public SoInvoiceModel setTotal_fee(BigDecimal total_fee) {
        this.total_fee = total_fee;
        return  this;
    }

    public BigDecimal getTotal_fee_cad() {
        return total_fee_cad;
    }

    public SoInvoiceModel setTotal_fee_cad(BigDecimal total_fee_cad) {
        this.total_fee_cad = total_fee_cad;
        return  this;
    }

    public BigDecimal getExchange_rate() {
        return exchange_rate;
    }

    public SoInvoiceModel setExchange_rate(BigDecimal exchange_rate) {
        this.exchange_rate = exchange_rate;
        return  this;
    }

    public String getInvoice_comments() {
        return invoice_comments;
    }

    public SoInvoiceModel setInvoice_comments(String invoice_comments) {
        this.invoice_comments = invoice_comments;
        return  this;
    }

    public String getBr_type() {
        return br_type;
    }

    public SoInvoiceModel setBr_type(String br_type) {
        this.br_type = br_type;
        return  this;
    }

    public Integer getCreator() {
        return creator;
    }

    public SoInvoiceModel setCreator(Integer creator) {
        this.creator = creator;
        return  this;
    }

    public Integer getBank_id() {
        return bank_id;
    }

    public SoInvoiceModel setBank_id(Integer bank_id) {
        this.bank_id = bank_id;
        return  this;
    }

    public String getBank_account() {
        return bank_account;
    }

    public SoInvoiceModel setBank_account(String bank_account) {
        this.bank_account = bank_account;
        return  this;
    }

    public String getBank_name() {
        return bank_name;
    }

    public SoInvoiceModel setBank_name(String bank_name) {
        this.bank_name = bank_name;
        return  this;
    }

    public List<SoInvoiceItem> getItems() {
        return items;
    }

    public SoInvoiceModel setItems(List<SoInvoiceItem> soInvoiceItems) {
        this.items = soInvoiceItems;
        return this;
    }

    public List getTax_content() { return tax_content; }

    public SoInvoiceModel setTax_content(List tax) {
        this.tax_content = tax;
        return this;
    }
}
