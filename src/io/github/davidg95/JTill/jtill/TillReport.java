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

    private final String terminal;
    private BigDecimal declared;
    private BigDecimal expected;
    private BigDecimal difference;
    private int transactions;
    private BigDecimal averageSpend;
    private BigDecimal tax;
    private final List<Sale> sales;

    public TillReport(String terminal, List<Sale> sales, BigDecimal declared) {
        this.terminal = terminal;
        this.sales = sales;
        this.declared = declared;
        this.expected = BigDecimal.ZERO;
        this.tax = BigDecimal.ZERO;
        init();
    }

    private void init() {
        for (Sale s : sales) {
            expected = expected.add(s.getTotal());
            for (SaleItem si : s.getSaleItems()) {
                tax = tax.add(si.getTaxValue());
            }
        }
        transactions = sales.size();
        averageSpend = expected.divide(new BigDecimal(transactions), RoundingMode.HALF_DOWN);
        difference = expected.subtract(declared);
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
        return difference;
    }

    public void setDifference(BigDecimal difference) {
        this.difference = difference;
    }

    public int getTransactions() {
        return transactions;
    }

    public void setTransactions(int transactions) {
        this.transactions = transactions;
    }

    public BigDecimal getAverageSpend() {
        return averageSpend;
    }

    public void setAverageSpend(BigDecimal averageSpend) {
        this.averageSpend = averageSpend;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public String getTerminal() {
        return terminal;
    }

    public List<Sale> getSales() {
        return sales;
    }

    @Override
    public String toString() {
        return "TillReport{" + "terminal=" + terminal + '}';
    }
}
