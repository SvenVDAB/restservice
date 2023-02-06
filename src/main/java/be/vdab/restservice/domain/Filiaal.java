package be.vdab.restservice.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "filialen")
/*@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)*/
public class Filiaal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; private String naam;
    private String gemeente;
    private BigDecimal omzet;

    protected Filiaal() {
    }

    public Filiaal(String naam, String gemeente, BigDecimal omzet) {
        this.naam = naam;
        this.gemeente = gemeente;
        this.omzet = omzet;
    }

    public long getId() {
        return id;
    }

    public String getNaam() {
        return naam;
    }

    public String getGemeente() {
        return gemeente;
    }

    public BigDecimal getOmzet() {
        return omzet;
    }
}
