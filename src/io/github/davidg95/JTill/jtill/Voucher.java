/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;

/**
 * Class of type Voucher which models a voucher.
 *
 * @author David
 */
public class Voucher implements Serializable {

    private int id;
    private String name;
    private VoucherType type;
    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String field5;
    private String field6;
    private String field7;
    private String field8;

    public enum VoucherType {
        XOnYGetZOff, Multibuy
    }

    public Voucher(String name, VoucherType type, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8) {
        this.name = name;
        this.type = type;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
        this.field6 = field6;
        this.field7 = field7;
        this.field8 = field8;
    }

    public Voucher(int id, String name, VoucherType type, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8) {
        this(name, type, field1, field2, field3, field4, field5, field6, field7, field8);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VoucherType getType() {
        return type;
    }

    public void setType(VoucherType type) {
        this.type = type;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    public String getField5() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5 = field5;
    }

    public String getField6() {
        return field6;
    }

    public void setField6(String field6) {
        this.field6 = field6;
    }

    public String getField7() {
        return field7;
    }

    public void setField7(String field7) {
        this.field7 = field7;
    }

    public String getField8() {
        return field8;
    }

    public void setField8(String field8) {
        this.field8 = field8;
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "','" + this.type.toString() + "'";
    }

    public String getSQlUpdateString() {
        return "UPDATE VOUCHERS"
                + " SET VOUCHERS.name='" + this.getName()
                + "', VOUCHERS.TYPE='" + this.getType().toString()
                + "', VOUCHERS.FIELD1='" + this.getField1()
                + "', VOUCHERS.FIELD2='" + this.getField2()
                + "', VOUCHERS.FIELD3='" + this.getField3()
                + "', VOUCHERS.FIELD4='" + this.getField4()
                + "', VOUCHERS.FIELD5='" + this.getField5()
                + "', VOUCHERS.FIELD6='" + this.getField6()
                + "', VOUCHERS.FIELD7='" + this.getField7()
                + "', VOUCHERS.FIELD8='" + this.getField8()
                + " WHERE VOUCHERS.ID=" + this.getId();
    }

    @Override
    public String toString() {
        return "ID: " + id + "\nName: " + name + "\nType:" + type + "\nField1: " + field1 + "\nField2: " + field2 + "\nField3: " + field3 + "\nField4: " + field4 + "\nField5: " + field5 + "\nField6: " + field6 + "\nField7: " + field7 + "\nField8: " + field8;
    }

}
