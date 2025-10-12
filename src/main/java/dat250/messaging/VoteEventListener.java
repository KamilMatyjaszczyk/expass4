package dat250.messaging;

import dat250.models.Poll;
import dat250.models.VoteOption;
import dat250.models.Vote;
import dat250.services.PollManager;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
public class VoteEventListener {

    private final PollManager pollManager;

    public VoteEventListener(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @RabbitListener(queues = "poll.votes")
    public void receiveVote(VoteEvent event) {
        System.out.println(" [x] Received VoteEvent: " + event);

        Long pollId = event.getPollId();
        Long optionId = event.getOptionId();

        Poll poll = pollManager.getPoll(pollId);
        VoteOption voteOption = pollManager.getVoteOption(optionId);

        if (poll == null) {
            System.out.println(" [!] Poll with ID " + pollId + " not found.");
            return;
        }
        if (voteOption == null) {
            System.out.println(" [!] VoteOption with ID " + optionId + " not found.");
            return;
        }

        Vote vote = new Vote();
        vote.setVoteOptionId(voteOption);
        vote.setPoll(poll);
        vote.setPublishedAt(Instant.now());

        pollManager.createVote(vote);

        System.out.println(" [✔] Vote registered for Poll ID " + pollId +
                ", Option ID " + optionId);
    }
}
