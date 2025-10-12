package dat250.models;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "pollId")
@Table(name = "polls")
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String question;
    private Instant publishedAt;
    private Instant validUntil;

//    private Boolean publicAccess;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;
    @OrderBy("Id ASC")
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = false)
    private List<VoteOption> options = new ArrayList<>();

    @OrderBy("publishedAt DESC")
    @OneToMany(mappedBy = "poll", orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Vote> votes = new ArrayList<>();

    public Poll() {
    }

    public void addVote(Vote v) {
        if (v == null) return;
        v.setPoll(this);
        if (!this.votes.contains(v)) {
            this.votes.add(v);
        }
    }

    public void addOption(VoteOption opt) {
        if (opt == null) return;
        opt.setPoll(this);
        if (!this.options.contains(opt)) {
            this.options.add(opt);
        }
    }

    public VoteOption addVoteOption(String caption) {
        if (caption == null || caption.isBlank()) {
            throw new IllegalArgumentException("No provided caption for the poll's option");
        }

        for (VoteOption opt : this.options) {
            if (opt.getCaption().equals(caption)) {
                return opt;
            }
        }

        VoteOption opt = new VoteOption();
        opt.setPoll(this);
        opt.setCaption(caption);
        this.options.add(opt);
        return opt;
    }

    // PollId
    public Long getPollId()   {
        return Id;
    }
    public void setPollId(Long pollId) {
        this.Id = pollId;
    }

    // Question
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

    // PublishedAt
    public Instant getPublishedAt() {
        return publishedAt;
    }
    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    // ValidUntil
    public Instant getValidUntil() {
        return validUntil;
    }
    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

//    // Public Access
//    public Boolean getPublicAccess()    {
//        return publicAccess;
//    }
//    public void setPublicAccess(Boolean publicAccess)   {
//        this.publicAccess = publicAccess;
//    }

    // CreatedBy
    public User getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
        if (createdBy != null && !createdBy.getPollsCreated().contains(this)) {
            createdBy.getPollsCreated().add(this);
        }
    }

    // Options
    public List<VoteOption> getOptions() { return options; }
public void setOptions(List<VoteOption> options) { this.options = options; }

public List<Vote> getVotes() { return votes; }
public void setVotes(List<Vote> votes) { this.votes = votes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Poll)) return false;
        Poll other = (Poll) o;
        return Id != null && Id.equals(other.Id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
