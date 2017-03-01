/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.math.BigDecimal;

/**
 *
 * @author David
 */
public interface Item {

    public int getId();

    public void setId(int id);

    public String getName();

    public void setName(String name);

    public BigDecimal getPrice();

    public void setPrice(BigDecimal price);

    public boolean isOpen();

    public void setOpen(boolean open);
}
