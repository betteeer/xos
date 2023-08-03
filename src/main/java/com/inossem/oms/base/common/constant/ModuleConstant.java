package com.inossem.oms.base.common.constant;

public interface ModuleConstant {

    //进出类型
    interface IN_OUT {
        int IN = 1;   //进
        int OUT = 2;  //出
    }

    //库存状态
    interface STOCK_STATUS {
        String NORMAL = "A";   //未冻结
        String STOCK = "B";    //冻结
    }

    //冻结状态
    interface IS_DELETED {
        int NO_DELETE = 0;   //未删除
        int DELETE = 1;    //已删除
    }

    //是否允许冻结
    interface IS_BOLCK_ALLOWED {
        int NO_ALLOWED = 0;   //不允许
        int ALLOWED = 1;    //允许
    }

    //是否有库存变更
    interface INVENTORY_CHANGE {
        int NOT_CHANGED = 0;   //无
        int CHANGED = 1;    //有
    }

    //是否已反向操作
    interface IS_REVERSED {
        int NORMAL = 0;   //未回退
        int REVERSED = 1;    //已回退
    }

    //ORDER Number 类型
    interface ORDER_NUMBER_TYPE {
        String SALE_ORDER = "SALE_ORDER";
        String RETURN_SO = "RETURN_SO";
        String PO_NUMBER = "PO_NUMBER";
        String SALES_DELIVERY_NUMBER = "SALES_DELIVERY_NUMBER";
        String PURCHASE_DELIVERY_NUMBER = "PURCHASE_DELIVERY_NUMBER";
        String MATERIAL_DOC = "MATERIAL_DOC";
    }

    //ORDER Number 初始值
    interface ORDER_NUMBER_START {
        long SALE_ORDER = 10000000;
        long RETURN_SO = 20000000;
        long PO_NUMBER = 30000000;
        long RETURN_PO = 40000000;
        long SALES_DELIVERY_NUMBER = 1;
        long PURCHASE_DELIVERY_NUMBER = 1;
        long MATERIAL_DOC = 5000000000L;
    }

    //DELIVERT 发运状态
    interface DELIVERY_SHIPPED_STATUS {
        String UNFULFILED = "Unfulfiled";  //未发运
        String FULLFILLED = "Fullfilled"; //发运
    }

    //DELIVERY_TYPE 发运类型
    interface DELIVERY_TYPE {
        String INVENTORY_SO = "DN";  //未发运
        String INVENTORY_PO = "ASN"; //发运
        String SERVICE_PO = "SEASN"; //发运
        String SERVICE_SO = "SEDN"; //发运
    }


    //SO & PO  DELIVERT 发运状态
    interface SOPO_DELIVERY_STATUS {
        String UNFULFILED = "UNFL";  //未发运
        String PARTIALLY_FULLFILLED = "PRFL";  //部分发运
        String FULLFILLED = "FUFL"; //完全发运
    }

    //SO & PO  BILLIING 开票状态
    interface SOPO_BILLIING_STATUS {
        String UNINVOICED = "UNVI"; //未开票
        String PARTIALLY_INVOICED = "PRVI"; //部分开票
        String FULLY_INVOICED = "FUIN"; //完全开票
    }


    //so order type 类型
    interface SOHEADER_ORDER_TYPE {
        String INVENTORY_SO = "INSO";
        String SERVICE_SO = "SESO";
        String DROPSHIP_SO = "DSSO";
    }

    //po order type Number 类型
    interface POHEADER_ORDER_TYPE {
        String INVENTORY_PO = "INPO";
        String SERVICE_PO = "SEPO";
        String DROPSHIP_PO = "DSPO";
    }



    /**
     * 移动类型
     */
    interface MOVEMENT_TYPE {
        String consumption = "201";
        String consumption_Reverse = "202";
        String Scrapping = "551";
        String Scrapping_Reverse = "552";
        String Initial_Stock = "561";
        String Initial_Stock_Reverse = "562";
        String PO_Receive = "101";
        String PO_Receive_Reverse = "102";
        String PO_return_Delivery = "161";
        String PO_return_Delivery_Reverse = "162";
        String SO_delivery = "601";    //销售单发货
        String SO_delivery_Reverse = "602";
        String SO_return_Receive = "651";
        String SO_return_Receive_Reverse = "652";
        String Count_Gain = "701";
        String Count_Loss = "702";
        String Block_Inventory = "343";
        String Unblock_Inventory = "344";

        String Transfer_Shipout = "313";
        String Transfer_Shipout_Reverse = "314";

        String Transfer_Receive = "315";
        String Transfer_Receive_Reverse = "316";
    }

    /**
     * 移动类型
     */
    interface REFERENCE_TYPE {
        String DN = "DN";  //SO delivery
        String ASN = "ASN";  //PO delivery\n
        String INAJ = "INAJ"; //others inventory adjustment
    }

    //数据库返回值状态
    interface DB_RES_STATUS {
        int FAIL_STATUS = 0;   //更新异常数值
    }


    //数据库返回值状态
    interface BILL_TYPE {
        int BILL = 0;
        int MERGE_BILL = 1;
    }

}
