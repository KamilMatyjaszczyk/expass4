package dat250.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.Instant;
import java.util.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "Id")
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String email;
    private String username;
    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Poll> pollsCreated = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Vote> votes = new ArrayList<>();

    public User() {
        this.pollsCreated = new ArrayList<>();
        this.votes = new ArrayList<>();
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.pollsCreated = new ArrayList<>();
        this.votes = new ArrayList<>();
    }

    // UserID
    public Long getUserId() {
        return Id;
    }

    public void setUserId(Long userId) {
        this.Id = userId;
    }

    // Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void addPoll(Poll p) {
        if (p == null) return;
        if (!pollsCreated.contains(p)) {
            pollsCreated.add(p);
        }
        if (p.getCreatedBy() != this) {
            p.setCreatedBy(this);
        }
    }

    public void addVote(Vote v) {
        if (v == null) return;
        v.setUser(this);
        if (!votes.contains(v)) {
            votes.add(v);
        }
    }
    public List<Poll> getPollsCreated() { return pollsCreated; }

    public Poll createPoll(String question) {
        if (question == null || question.isEmpty()) {
            throw new IllegalArgumentException("No empty question");
        }
        Poll poll = new Poll();
        poll.setQuestion(question);
        poll.setCreatedBy(this);
        poll.setPublishedAt(Instant.now());

        if (!pollsCreated.contains(poll)) {
            pollsCreated.add(poll);
        }
        return poll;
    }

    public Vote voteFor(VoteOption option) {
        if (option == null || option.getPoll() == null) {
            throw new IllegalArgumentException("The provided vote-option is either null or is not connected to a poll");
        }

        Poll poll = option.getPoll();

        Vote toRemove = null;
        for (Vote v : new ArrayList<>(this.votes)) {
            if (v.getPoll() != null &&
                    v.getPoll().getPollId() != null &&
                    poll.getPollId() != null &&
                    v.getPoll().getPollId().equals(poll.getPollId())) {
                toRemove = v;
                break;
            }
        }

        if (toRemove != null) {
            this.votes.remove(toRemove);
            poll.getVotes().remove(toRemove);
            if (toRemove.getVoteOption() != null) {
                toRemove.getVoteOption().getVotes().remove(toRemove);
            }

        }

        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setUser(this);
        vote.setVoteOption(option);
        vote.setPublishedAt(Instant.now());

        this.addVote(vote);
        poll.addVote(vote);
        option.addVote(vote);

        return vote;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(Id, user.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Id);
    }
}



