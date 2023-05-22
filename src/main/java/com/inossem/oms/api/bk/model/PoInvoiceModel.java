package com.inossem.oms.api.bk.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.inossem.oms.api.bk.utils.BigDecimalSerialize;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author kgh
 * @date 2022-12-10 14:38
 */
public class PoInvoiceModel {

    public static class PoInvoiceItem {
        private String item_no;
        private String model;
        private String description;
        private BigDecimal qty;
        //private String uom;
        @JsonSerialize(using = BigDecimalSerialize.class)
        private BigDecimal unit_price;
        @JsonSerialize(using = BigDecimalSerialize.class)
        private BigDecimal total;
        private String type;
        //private String expenseAccountId;        //CoA ID   这个应该是BK里面根据OBYC去自动决定的
        //private String expenseAccount;        //CoA code
       // private String bankAccount;
        private String dr_cr;
        private String credit_coa_id;
        private String credit_coa_code;
        private String credit_coa_name;

        public PoInvoiceItem setCredit_coa_id(String credit_coa_id) {
            this.credit_coa_id = credit_coa_id;
            return this;
        }

        public PoInvoiceItem setCredit_coa_code(String credit_coa_code) {
            this.credit_coa_code = credit_coa_code;
            return this;
        }

        public PoInvoiceItem setCredit_coa_name(String credit_coa_name) {
            this.credit_coa_name = credit_coa_name;
            return this;
        }

        public String getCredit_coa_id() {
            return credit_coa_id;
        }

        public String getCredit_coa_code() {
            return credit_coa_code;
        }

        public String getCredit_coa_name() {
            return credit_coa_name;
        }

        public PoInvoiceItem setDr_cr(String dr_cr) {
            this.dr_cr = dr_cr;
            return this;
        }

        public String getDr_cr() {
            return dr_cr;
        }

        public String getItem_no() {
            return item_no;
        }

        public PoInvoiceItem setType(String type) {
            this.type = type;
            return this;
        }

        public String getType() {
            return type;
        }

        public PoInvoiceItem setItem_no(String item_no) {
            this.item_no = item_no;
            return this;
        }

        public String getModel() {
            return model;
        }

        public PoInvoiceItem setModel(String model) {
            this.model = model;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public PoInvoiceItem setDescription(String description) {
            this.description = description;
            return this;
        }

        public BigDecimal getQty() {
            return qty;
        }

        public PoInvoiceItem setQty(BigDecimal qty) {
            this.qty = qty;
            return this;
        }

        public BigDecimal getUnit_price() {
            return unit_price;
        }

        public PoInvoiceItem setUnit_price(BigDecimal unit_price) {
            this.unit_price = unit_price;
            return this;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public PoInvoiceItem setTotal(BigDecimal total) {
            this.total = total.setScale(2, BigDecimal.ROUND_HALF_UP);
            //this.total = total;
            return this;
        }

/*        public String getExpenseAccountId() {
            return expenseAccountId;
        }

        public PoInvoiceItem setExpenseAccountId(String expenseAccountId) {
            this.expenseAccountId = expenseAccountId;
            return this;
        }

        public String getExpenseAccount() {
            return expenseAccount;
        }

        public PoInvoiceItem setExpenseAccount(String expenseAccount) {
            this.expenseAccount = expenseAccount;
            return this;
        }

        public String getBankAccount() {
            return bankAccount;
        }

        public PoInvoiceItem setBankAccount(String bankAccount) {
            this.bankAccount = bankAccount;
            return this;
        }*/
    }

    private String company_id;
    private String company_code;
    private String issuer_id;
    private String issuer_name;
    private String issuer_address;
    private String issuer_tel;
    private String issuer_email;
    private String  reference_no;
    private String invoice_currency;
    private String pay_method;
    private String invoice_create_date;
    private String invoice_due_date;
    private String posting_date;
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal net_amount;
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal gst; //null,
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal pst; //null,
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal qst; //null,
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal total_tax; //0.00,
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal total_fee; //16.00,
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal total_fee_cad;
    private BigDecimal exchange_rate;
    private String invoice_comments;
    private String br_type;
    private String creator;
    private String file_url;
    private List<PoInvoiceItem> items;

    public void setItems(List<PoInvoiceItem> items) {
        this.items = items;
    }

    public List<PoInvoiceItem> getItems() {
        return items;
    }

    public PoInvoiceModel setReference_no(String reference_no) {
        this.reference_no = reference_no;
        return this;
    }

    public String getReference_no() {
        return reference_no;
    }

    public PoInvoiceModel setCompany_id(String company_id) {
        this.company_id = company_id;
        return this;
    }

    public PoInvoiceModel setCompany_code(String company_code) {
        this.company_code = company_code;
        return this;
    }

    public PoInvoiceModel setIssuer_id(String issuer_id) {
        this.issuer_id = issuer_id;
        return this;
    }

    public PoInvoiceModel setIssuer_name(String issuer_name) {
        this.issuer_name = issuer_name;
        return this;
    }

    public PoInvoiceModel setIssuer_address(String issuer_address) {
        this.issuer_address = issuer_address;
        return this;
    }

    public PoInvoiceModel setIssuer_tel(String issuer_tel) {
        this.issuer_tel = issuer_tel;
        return this;
    }

    public PoInvoiceModel setIssuer_email(String issuer_email) {
        this.issuer_email = issuer_email;
        return this;
    }

    public PoInvoiceModel setInvoice_currency(String invoice_currency) {
        this.invoice_currency = invoice_currency;
        return this;
    }

    public PoInvoiceModel setPay_method(String pay_method) {
        this.pay_method = pay_method;
        return this;
    }

    public PoInvoiceModel setInvoice_create_date(String invoice_create_date) {
        this.invoice_create_date = invoice_create_date;
        return this;
    }

    public PoInvoiceModel setInvoice_due_date(String invoice_due_date) {
        this.invoice_due_date = invoice_due_date;
        return this;
    }

    public PoInvoiceModel setPosting_date(String posting_date) {
        this.posting_date = posting_date;
        return this;
    }

    public PoInvoiceModel setNet_amount(BigDecimal net_amount) {
        this.net_amount = net_amount;
        return this;
    }

    public PoInvoiceModel setGst(BigDecimal gst) {
        this.gst = gst;
        return this;
    }

    public PoInvoiceModel setPst(BigDecimal pst) {
        this.pst = pst;
        return this;
    }

    public PoInvoiceModel setQst(BigDecimal qst) {
        this.qst = qst;
        return this;
    }

    public PoInvoiceModel setTotal_tax(BigDecimal total_tax) {
        this.total_tax = total_tax;
        return this;
    }

    public PoInvoiceModel setTotal_fee(BigDecimal total_fee) {
        this.total_fee = total_fee;
        return this;
    }

    public PoInvoiceModel setTotal_fee_cad(BigDecimal total_fee_cad) {
        this.total_fee_cad = total_fee_cad;
        return this;
    }

    public PoInvoiceModel setExchange_rate(BigDecimal exchange_rate) {
        this.exchange_rate = exchange_rate;
        return this;
    }

    public PoInvoiceModel setInvoice_comments(String invoice_comments) {
        this.invoice_comments = invoice_comments;
        return this;
    }

    public PoInvoiceModel setBr_type(String br_type) {
        this.br_type = br_type;
        return this;
    }

    public PoInvoiceModel setCreator(String creator) {
        this.creator = creator;
        return this;
    }

    public PoInvoiceModel setFile_url(String file_url) {
        this.file_url = file_url;
        return this;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getCompany_code() {
        return company_code;
    }

    public String getIssuer_id() {
        return issuer_id;
    }

    public String getIssuer_name() {
        return issuer_name;
    }

    public String getIssuer_address() {
        return issuer_address;
    }

    public String getIssuer_tel() {
        return issuer_tel;
    }

    public String getIssuer_email() {
        return issuer_email;
    }

    public String getInvoice_currency() {
        return invoice_currency;
    }

    public String getPay_method() {
        return pay_method;
    }

    public String getInvoice_create_date() {
        return invoice_create_date;
    }

    public String getInvoice_due_date() {
        return invoice_due_date;
    }

    public String getPosting_date() {
        return posting_date;
    }

    public BigDecimal getNet_amount() {
        return net_amount;
    }

    public BigDecimal getGst() {
        return gst;
    }

    public BigDecimal getPst() {
        return pst;
    }

    public BigDecimal getQst() {
        return qst;
    }

    public BigDecimal getTotal_tax() {
        return total_tax;
    }

    public BigDecimal getTotal_fee() {
        return total_fee;
    }

    public BigDecimal getTotal_fee_cad() {
        return total_fee_cad;
    }

    public BigDecimal getExchange_rate() {
        return exchange_rate;
    }

    public String getInvoice_comments() {
        return invoice_comments;
    }

    public String getBr_type() {
        return br_type;
    }

    public String getCreator() {
        return creator;
    }

    public String getFile_url() {
        return file_url;
    }
/*  private String orgId; //"80",
    private String supplierId; //"0030025023",
    private String companyName; //"5599",
    private String companyCode; //"3002",
    private String companyAddress; //null,
    private String companyPhone; //"",
    private String companyEmail; //"",
    private String companyLogo; //"",
    private String companyGstNo; //null,
    private String companyPstNo; //null,
    @JsonSerialize(using = BigDecimalSerialize.class)
    private Double gst; //null,
    @JsonSerialize(using = BigDecimalSerialize.class)
    private Double pst; //null,
    private String referenceNo; //"",
    private String invoiceCurrency; //"2",
    private String payMethod; //"1",
    private String invoiceCreateDate; //"2022-09-27",
    private String invoiceDueDate; //"2022-09-27",
    private String postingDate; //"2022-09-27",
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal amount; //16.00,
    private String shipping; //null,
    private String discount; //null,
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalTaxable; //16.00,
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal tps; //0.00,
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal tvq; //0.00,
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal tvp; //0.00,
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalTax; //0.00,
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalFee; //16.00,
    private BigDecimal totalFeeExchangeCAD; //null,
    private BigDecimal exchangeRate; //null,
    private String deposit; //null,
    private String invoiceComments; //"",
    private Integer fileId; //"629",
    private Integer filePageIndex; //null,
    private String fileUrl; //null,
    private String po; //"0001",
    private String paymentTermsDay1; //"paymentTermsDay1",
    private String paymentTermsDay2; //"paymentTermsDay2",
    private String paymentTermsDay3; //"paymentTermsDay3",
    private String paymentTermsDiscount1; //"paymentTermsDiscount1",
    private String paymentTermsDiscount2; //"paymentTermsDiscount2",
    private String paymentTermsDiscount3; //"paymentTermsDiscount3",
    private List<PoInvoiceModel.PoInvoiceItem> items;

    public List<PoInvoiceItem> getItemList() {
        return itemList;
    }

    public PoInvoiceModel setItemList(List<PoInvoiceItem> itemList) {
        this.itemList = itemList;
        return this;
    }

    public String getOrgId() {
        return orgId;
    }

    public PoInvoiceModel setOrgId(String orgId) {
        this.orgId = orgId;
        return this;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public PoInvoiceModel setSupplierId(String supplierId) {
        this.supplierId = supplierId;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public PoInvoiceModel setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public PoInvoiceModel setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
        return this;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public PoInvoiceModel setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
        return this;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public PoInvoiceModel setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
        return this;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public PoInvoiceModel setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
        return this;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public PoInvoiceModel setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
        return this;
    }

    public String getCompanyGstNo() {
        return companyGstNo;
    }

    public PoInvoiceModel setCompanyGstNo(String companyGstNo) {
        this.companyGstNo = companyGstNo;
        return this;
    }

    public String getCompanyPstNo() {
        return companyPstNo;
    }

    public PoInvoiceModel setCompanyPstNo(String companyPstNo) {
        this.companyPstNo = companyPstNo;
        return this;
    }

    public Double getGst() {
        return gst;
    }

    public PoInvoiceModel setGst(Double gst) {
        this.gst = gst;
        return this;
    }

    public Double getPst() {
        return pst;
    }

    public PoInvoiceModel setPst(Double pst) {
        this.pst = pst;
        return this;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public PoInvoiceModel setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
        return this;
    }

    public String getInvoiceCurrency() {
        return invoiceCurrency;
    }

    public PoInvoiceModel setInvoiceCurrency(String invoiceCurrency) {
        this.invoiceCurrency = invoiceCurrency;
        return this;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public PoInvoiceModel setPayMethod(String payMethod) {
        this.payMethod = payMethod;
        return this;
    }

    public String getInvoiceCreateDate() {
        return invoiceCreateDate;
    }

    public PoInvoiceModel setInvoiceCreateDate(String invoiceCreateDate) {
        this.invoiceCreateDate = invoiceCreateDate;
        return this;
    }

    public String getInvoiceDueDate() {
        return invoiceDueDate;
    }

    public PoInvoiceModel setInvoiceDueDate(String invoiceDueDate) {
        this.invoiceDueDate = invoiceDueDate;
        return this;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public PoInvoiceModel setPostingDate(String postingDate) {
        this.postingDate = postingDate;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PoInvoiceModel setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public String getShipping() {
        return shipping;
    }

    public PoInvoiceModel setShipping(String shipping) {
        this.shipping = shipping;
        return this;
    }

    public String getDiscount() {
        return discount;
    }

    public PoInvoiceModel setDiscount(String discount) {
        this.discount = discount;
        return this;
    }

    public BigDecimal getTotalTaxable() {
        return totalTaxable;
    }

    public PoInvoiceModel setTotalTaxable(BigDecimal totalTaxable) {
        this.totalTaxable = totalTaxable;
        return this;
    }

    public BigDecimal getTps() {
        return tps;
    }

    public PoInvoiceModel setTps(BigDecimal tps) {
        this.tps = tps;
        return this;
    }

    public BigDecimal getTvq() {
        return tvq;
    }

    public PoInvoiceModel setTvq(BigDecimal tvq) {
        this.tvq = tvq;
        return this;
    }

    public BigDecimal getTvp() {
        return tvp;
    }

    public PoInvoiceModel setTvp(BigDecimal tvp) {
        this.tvp = tvp;
        return this;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public PoInvoiceModel setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
        return this;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public PoInvoiceModel setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
        return this;
    }

//    public String getTotalFeeExchangeCAD() {
//        return totalFeeExchangeCAD;
//    }
//
//    public PoInvoiceModel setTotalFeeExchangeCAD(String totalFeeExchangeCAD) {
//        this.totalFeeExchangeCAD = totalFeeExchangeCAD;
//        return this;
//    }

    public BigDecimal getTotalFeeExchangeCAD() {
        return totalFeeExchangeCAD;
    }

    public PoInvoiceModel setTotalFeeExchangeCAD(BigDecimal totalFeeExchangeCAD) {
        this.totalFeeExchangeCAD = totalFeeExchangeCAD;
        return this;
    }


//    public String getExchangeRate() {
//        return exchangeRate;
//    }
//
//    public PoInvoiceModel setExchangeRate(String exchangeRate) {
//        this.exchangeRate = exchangeRate;
//        return this;
//    }


    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public PoInvoiceModel setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
        return this;
    }

    public String getDeposit() {
        return deposit;
    }

    public PoInvoiceModel setDeposit(String deposit) {
        this.deposit = deposit;
        return this;
    }

    public String getInvoiceComments() {
        return invoiceComments;
    }

    public PoInvoiceModel setInvoiceComments(String invoiceComments) {
        this.invoiceComments = invoiceComments;
        return this;
    }

    public Integer getFileId() {
        return fileId;
    }

    public PoInvoiceModel setFileId(Integer fileId) {
        this.fileId = fileId;
        return this;
    }

    public Integer getFilePageIndex() {
        return filePageIndex;
    }

    public PoInvoiceModel setFilePageIndex(Integer filePageIndex) {
        this.filePageIndex = filePageIndex;
        return this;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public PoInvoiceModel setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public String getPo() {
        return po;
    }

    public PoInvoiceModel setPo(String po) {
        this.po = po;
        return this;
    }

    public String getPaymentTermsDay1() {
        return paymentTermsDay1;
    }

    public PoInvoiceModel setPaymentTermsDay1(String paymentTermsDay1) {
        this.paymentTermsDay1 = paymentTermsDay1;
        return this;
    }

    public String getPaymentTermsDay2() {
        return paymentTermsDay2;
    }

    public PoInvoiceModel setPaymentTermsDay2(String paymentTermsDay2) {
        this.paymentTermsDay2 = paymentTermsDay2;
        return this;
    }

    public String getPaymentTermsDay3() {
        return paymentTermsDay3;
    }

    public PoInvoiceModel setPaymentTermsDay3(String paymentTermsDay3) {
        this.paymentTermsDay3 = paymentTermsDay3;
        return this;
    }

    public String getPaymentTermsDiscount1() {
        return paymentTermsDiscount1;
    }

    public PoInvoiceModel setPaymentTermsDiscount1(String paymentTermsDiscount1) {
        this.paymentTermsDiscount1 = paymentTermsDiscount1;
        return this;
    }

    public String getPaymentTermsDiscount2() {
        return paymentTermsDiscount2;
    }

    public PoInvoiceModel setPaymentTermsDiscount2(String paymentTermsDiscount2) {
        this.paymentTermsDiscount2 = paymentTermsDiscount2;
        return this;
    }

    public String getPaymentTermsDiscount3() {
        return paymentTermsDiscount3;
    }

    public PoInvoiceModel setPaymentTermsDiscount3(String paymentTermsDiscount3) {
        this.paymentTermsDiscount3 = paymentTermsDiscount3;
        return this;
    }*/
}
