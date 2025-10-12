package dat250.messaging;

import java.io.Serializable;
import java.time.Instant;


public class VoteEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long pollId;
    private Long optionId;
    private Instant timestamp;

    public VoteEvent() {
        this.timestamp = Instant.now();
    }

    public VoteEvent(Long pollId, Long optionId) {
        this.pollId = pollId;
        this.optionId = optionId;
        this.timestamp = Instant.now();
    }

    public Long getPollId() {
        return pollId;
    }

    public void setPollId(Long pollId) {
        this.pollId = pollId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "VoteEvent{" +
                "pollId=" + pollId +
                ", optionId=" + optionId +
                ", timestamp=" + timestamp +
                '}';
    }
}
