/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.EntityClasses;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author 1301480
 */
@Entity
@Table(name = "PRODUCTS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Products.findAll", query = "SELECT p FROM Products p"),
    @NamedQuery(name = "Products.findById", query = "SELECT p FROM Products p WHERE p.id = :id"),
    @NamedQuery(name = "Products.findByBarcode", query = "SELECT p FROM Products p WHERE p.barcode = :barcode"),
    @NamedQuery(name = "Products.findByName", query = "SELECT p FROM Products p WHERE p.name = :name"),
    @NamedQuery(name = "Products.findByPrice", query = "SELECT p FROM Products p WHERE p.price = :price"),
    @NamedQuery(name = "Products.findByStock", query = "SELECT p FROM Products p WHERE p.stock = :stock"),
    @NamedQuery(name = "Products.findByComments", query = "SELECT p FROM Products p WHERE p.comments = :comments"),
    @NamedQuery(name = "Products.findByShortName", query = "SELECT p FROM Products p WHERE p.shortName = :shortName"),
    @NamedQuery(name = "Products.findByCostPrice", query = "SELECT p FROM Products p WHERE p.costPrice = :costPrice"),
    @NamedQuery(name = "Products.findByMinProductLevel", query = "SELECT p FROM Products p WHERE p.minProductLevel = :minProductLevel"),
    @NamedQuery(name = "Products.findByMaxProductLevel", query = "SELECT p FROM Products p WHERE p.maxProductLevel = :maxProductLevel")})
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private String id;
    @Column(name = "BARCODE")
    private String barcode;
    @Basic(optional = false)
    @Column(name = "NAME")
    private String name;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PRICE")
    private Double price;
    @Column(name = "STOCK")
    private Integer stock;
    @Column(name = "COMMENTS")
    private String comments;
    @Basic(optional = false)
    @Column(name = "SHORT_NAME")
    private String shortName;
    @Column(name = "COST_PRICE")
    private Double costPrice;
    @Column(name = "MIN_PRODUCT_LEVEL")
    private Integer minProductLevel;
    @Column(name = "MAX_PRODUCT_LEVEL")
    private Integer maxProductLevel;
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Categorys categoryId;
    @JoinColumn(name = "DISCOUNT_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Discounts discountId;
    @JoinColumn(name = "TAX_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Tax taxId;

    public Products() {
    }

    public Products(String id) {
        this.id = id;
    }

    public Products(String id, String name, String shortName) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getMinProductLevel() {
        return minProductLevel;
    }

    public void setMinProductLevel(Integer minProductLevel) {
        this.minProductLevel = minProductLevel;
    }

    public Integer getMaxProductLevel() {
        return maxProductLevel;
    }

    public void setMaxProductLevel(Integer maxProductLevel) {
        this.maxProductLevel = maxProductLevel;
    }

    public Categorys getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Categorys categoryId) {
        this.categoryId = categoryId;
    }

    public Discounts getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Discounts discountId) {
        this.discountId = discountId;
    }

    public Tax getTaxId() {
        return taxId;
    }

    public void setTaxId(Tax taxId) {
        this.taxId = taxId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Products)) {
            return false;
        }
        Products other = (Products) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "io.github.davidg95.Till.EntityClasses.Products[ id=" + id + " ]";
    }
    
}
