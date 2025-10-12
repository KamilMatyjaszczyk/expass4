package dat250.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "voteId")
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_option_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private VoteOption voteOption;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private Poll poll;

    private Instant publishedAt;

    public Vote()   {
    }

    // VoteId
    public Long getVoteId()   {
        return Id;
    }
    public void setVoteId(Long voteId)    {
        this.Id = voteId;
    }

    // User
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    // VoteOptionId
    public VoteOption getVoteOptionId() {
        return voteOption;
    }
    public void setVoteOptionId(VoteOption voteOptionId) {
        this.voteOption = voteOptionId;
    }

    // PublishedAt
    public Instant getPublishedAt() {
        return publishedAt;
    }
    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public VoteOption getVoteOption() {
        return voteOption;
    }
    public void setVoteOption(VoteOption voteOptionId) {
        this.voteOption = voteOptionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vote)) return false;
        Vote other = (Vote) o;
        return Id != null && Id.equals(other.Id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
