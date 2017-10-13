/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 *
 * @author David
 */
public class TillReport implements Serializable {

    private int id;
    private final Till terminal;
    private final long time;
    private final Staff staff;
    private BigDecimal declared;
    private BigDecimal expected;
    private int transactions;
    private BigDecimal tax;
    private List<Sale> sales;

    public String getInsert() {
        return terminal.getId() + "," + declared.doubleValue() + "," + expected.doubleValue() + "," + transactions + "," + tax.doubleValue() + "," + staff.getId() + "," + time;
    }

    public TillReport(Till terminal, List<Sale> sales, BigDecimal declared, Staff staff, long time) {
        this.terminal = terminal;
        this.sales = sales;
        this.declared = declared.setScale(2, 6);
        this.expected = BigDecimal.ZERO;
        this.tax = BigDecimal.ZERO;
        this.staff = staff;
        this.time = time;
        init();
    }

    public TillReport(int id, Till terminal, BigDecimal declared, BigDecimal expected, int transactions, BigDecimal tax, Staff staff, long time) {
        this.id = id;
        this.terminal = terminal;
        this.declared = declared.setScale(2, 6);
        this.expected = expected.setScale(2, 6);
        this.transactions = transactions;
        this.tax = tax.setScale(2, 6);
        this.staff = staff;
        this.time = time;
    }

    private void init() {
        for (Sale s : sales) {
            expected = expected.add(s.getTotal());
            for (SaleItem si : s.getSaleItems()) {
                tax = tax.add(si.getTaxValue());
            }
        }
        expected = expected.setScale(2, 6);
        transactions = sales.size();
    }

    public BigDecimal getDeclared() {
        return declared;
    }

    public void setDeclared(BigDecimal declared) {
        this.declared = declared;
    }

    public BigDecimal getExpected() {
        return expected;
    }

    public void setExpected(BigDecimal expected) {
        this.expected = expected;
    }

    public BigDecimal getDifference() {
        return expected.subtract(declared).setScale(2, 6);
    }

    public int getTransactions() {
        return transactions;
    }

    public void setTransactions(int transactions) {
        this.transactions = transactions;
    }

    public BigDecimal getAverageSpend() {
        return expected.divide(new BigDecimal(transactions), 2, 6);
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public Till getTerminal() {
        return terminal;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public Staff getStaff() {
        return staff;
    }

    public long getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TillReport{" + "terminal=" + terminal + '}';
    }
}
