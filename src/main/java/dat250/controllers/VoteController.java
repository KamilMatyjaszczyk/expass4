package dat250.controllers;

import dat250.messaging.VoteEvent;
import dat250.models.Vote;
import dat250.services.PollManager;
import dat250.services.VoteEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Manages API-endpoints for votes.
 */

@RestController
@CrossOrigin
@RequestMapping("/votes")
public class VoteController {

    @Autowired
    private PollManager pollManager;
    @Autowired
    private VoteEventPublisher voteEventPublisher;

    @GetMapping("")
    public ResponseEntity<List<Vote>> getVotes() {
        return ResponseEntity.ok(pollManager.getAllVotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vote> getVote(@PathVariable Long id) {
        Vote vote = pollManager.getVote(id);
        if (vote == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(vote);
    }

    @PostMapping("")
    public ResponseEntity<Vote> createVote(@RequestBody Vote vote) {
        try {
            Vote created = pollManager.createVote(vote);

            if (created != null && created.getPoll() != null && created.getVoteOptionId() != null) {
                Long pollId = created.getPoll().getPollId();
                Long optionId = created.getVoteOptionId().getOptionId();

                VoteEvent event = new VoteEvent(pollId, optionId);
                voteEventPublisher.publishVoteEvent(pollId, optionId);
                System.out.println(" [x] Published event: " + event);
            }

            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vote> updateVote(@PathVariable Long id, @RequestBody Vote vote) {
        Vote updatedVote = pollManager.updateVote(id, vote);
        if (updatedVote == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedVote);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVote(@PathVariable Long id) {
        pollManager.deleteVote(id);
        return ResponseEntity.noContent().build();
    }
}