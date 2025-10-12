package dat250.services;

import dat250.models.Poll;
import dat250.models.User;
import dat250.models.UserRequests.UserGetResponse;
import dat250.models.UserRequests.UserUpdateRequest;
import dat250.models.Vote;
import dat250.models.VoteOption;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Manages CRUD (Create, Read, Update, Delete) operations for polls, users, votes, and vote options.
 */
@Component
public class PollManager {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Poll> polls = new HashMap<>();
    private final Map<Long, Vote> votes = new HashMap<>();
    private final Map<Long, VoteOption> voteOptions = new HashMap<>();

    // -------------------- USER CRUD --------------------
    public User createUser(User user) {
        if (user.getUserId() != null && users.containsKey(user.getUserId())) {
            return null;
        }
        users.put(user.getUserId(), user);
        return user;
    }

    public User getUser(Long userId) {
        return users.get(userId);
    }

    public UserGetResponse getRestrictedUser(Long userId) {
        User user = this.getUser(userId);
        if (user == null) return null;

        UserGetResponse userGetResponse = new UserGetResponse();
        userGetResponse.setEmail(user.getEmail());
        userGetResponse.setUserId(String.valueOf(user.getUserId()));
        return userGetResponse;
    }

    public List<UserGetResponse> getAllRestrictedUsers() {
        List<UserGetResponse> restrictedUsers = new ArrayList<>();
        for (User user : users.values()) {
            UserGetResponse response = new UserGetResponse();
            response.setEmail(user.getEmail());
            response.setUserId(String.valueOf(user.getUserId()));
            restrictedUsers.add(response);
        }
        return restrictedUsers;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User updateUser(Long userId, UserUpdateRequest updateRequest) {
        User existingUser = users.get(userId);
        if (existingUser == null) return null;

        if (updateRequest.getEmail() != null) {
            existingUser.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPassword() != null) {
            existingUser.setPassword(updateRequest.getPassword());
        }
        return existingUser;
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    // -------------------- POLL CRUD --------------------
    public Poll createPoll(Poll poll) {
        // pollId håndteres av JPA
        if (poll.getValidUntil() == null) {
            poll.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));
        }
        if (poll.getPublishedAt() == null) {
            poll.setPublishedAt(Instant.now());
        }
        polls.put(poll.getPollId(), poll);
        return poll;
    }

    public Poll getPoll(Long pollId) {
        Poll poll = polls.get(pollId);
        return createFilteredPoll(poll);
    }

    public List<Poll> getAllPolls() {
        List<Poll> modifiedPolls = new ArrayList<>();
        for (Poll poll : polls.values()) {
            Poll filteredPoll = createFilteredPoll(poll);
            if (filteredPoll != null) modifiedPolls.add(filteredPoll);
        }
        return modifiedPolls;
    }

    public Poll updatePoll(Long pollId, Poll updateRequest) {
        Poll existingPoll = polls.get(pollId);
        if (existingPoll == null) return null;

        if (updateRequest.getValidUntil() != null) {
            existingPoll.setValidUntil(updateRequest.getValidUntil());
        }
//        if (updateRequest.getPublicAccess() != null) {
//            existingPoll.setPublicAccess(updateRequest.getPublicAccess());
//        }
        return existingPoll;
    }

    public void deletePoll(Long pollId) {
        if (pollId == null) return;
        List<Long> optionIds = new ArrayList<>();
        for (VoteOption option : voteOptions.values()) {
            if (pollId.equals(option.getPoll().getPollId())) {
                optionIds.add(option.getOptionId());
            }
        }
        for (Long optionId : optionIds) {
            deleteVoteOption(optionId);
        }
        polls.remove(pollId);
    }

    // -------------------- VOTE OPTION CRUD --------------------
    public VoteOption createVoteOption(VoteOption option) {
        voteOptions.put(option.getOptionId(), option);
        return option;
    }

    public VoteOption getVoteOption(Long optionId) {
        return voteOptions.get(optionId);
    }

    public List<VoteOption> getAllVoteOptions() {
        return new ArrayList<>(voteOptions.values());
    }

    public void deleteVoteOption(Long optionId) {
        if (optionId == null) return;
        votes.values().removeIf(v -> optionId.equals(v.getVoteOptionId().getOptionId()));
        voteOptions.remove(optionId);
    }

    // -------------------- VOTE CRUD --------------------
    public Vote createVote(Vote vote) {
        if (vote.getPublishedAt() == null) {
            vote.setPublishedAt(Instant.now());
        }
        votes.put(vote.getVoteId(), vote);
        return vote;
    }

    public Vote getVote(Long voteId) {
        return votes.get(voteId);
    }

    public List<Vote> getAllVotes() {
        return new ArrayList<>(votes.values());
    }

    public Vote updateVote(Long voteId, Vote updatedVote) {
        Vote existingVote = votes.get(voteId);
        if (existingVote == null) return null;

        existingVote.setPublishedAt(Instant.now());
        if (updatedVote.getVoteOptionId() != null) {
            existingVote.setVoteOptionId(updatedVote.getVoteOptionId());
        }
        return existingVote;
    }

    public void deleteVote(Long voteId) {
        votes.remove(voteId);
    }

    // -------------------- HELPERS --------------------
    private Poll createFilteredPoll(Poll originalPoll) {
        if (originalPoll == null) return null;

        Poll modifiedPoll = new Poll();
        modifiedPoll.setPollId(originalPoll.getPollId());
        modifiedPoll.setQuestion(originalPoll.getQuestion());
        modifiedPoll.setPublishedAt(originalPoll.getPublishedAt());
        modifiedPoll.setValidUntil(originalPoll.getValidUntil());
//        modifiedPoll.setPublicAccess(originalPoll.getPublicAccess());

        if (originalPoll.getCreatedBy() != null) {
            User creatorSummary = new User();
            creatorSummary.setUserId(originalPoll.getCreatedBy().getUserId());
            modifiedPoll.setCreatedBy(creatorSummary);
        }

        modifiedPoll.setOptions(originalPoll.getOptions());
        return modifiedPoll;
    }
}
