package dat250.models;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "optionId")
@Table(name = "vote_options")
public class VoteOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String caption;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;
    private int presentationOrder;
    @OneToMany(mappedBy = "voteOption", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Vote> votes = new ArrayList<>();
    public VoteOption() {
    }

    // OptionId
    public Long getOptionId()   {
        return Id;
    }
    public void setOptionId(Long optionId)    {
        this.Id = optionId;
    }

    // Caption
    public String getCaption()  {
        return caption;
    }
    public void setCaption(String caption)  {
        this.caption = caption;
    }

    public Poll getPoll() { return poll; }
    public void setPoll(Poll poll) {
        this.poll = poll;
        if (poll != null && !poll.getOptions().contains(this)) {
            poll.getOptions().add(this);
        }
    }
    // Votes
    public List<Vote> getVotes() { return votes; }
    public void setVotes(List<Vote> votes) { this.votes = votes; }

    public void addVote(Vote v) {
        if (v == null) return;
        v.setVoteOption(this);
        if (!this.votes.contains(v)) {
            this.votes.add(v);
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoteOption)) return false;
        VoteOption other = (VoteOption) o;
        return Id != null && Id.equals(other.Id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
