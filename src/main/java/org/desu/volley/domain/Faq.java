package org.desu.volley.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Faq.
 */
@Entity
@Table(name = "faq")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Faq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Min(value = 0)
    @Column(name = "index", nullable = false)
    private Integer index;

    @NotNull
    @Column(name = "question", nullable = false)
    private String question;

    @NotNull
    @Column(name = "answer", nullable = false)
    private String answer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Faq faq = (Faq) o;
        if(faq.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, faq.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Faq{" +
            "id=" + id +
            ", index='" + index + "'" +
            ", question='" + question + "'" +
            ", answer='" + answer + "'" +
            '}';
    }
}
