package com.service.notes.persistence.repository.custom.impl;

import com.service.notes.domain.dto.NoteFormDTO;
import com.service.notes.domain.dto.NoteQueryDTO;
import com.service.notes.domain.dto.NoteWithLikeDataDTO;
import com.service.notes.persistence.entity.Note;
import com.service.notes.persistence.repository.custom.CustomNoteRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CustomNoteRepositoryImpl implements CustomNoteRepository {

    private final MongoOperations mongoOperations;

    public CustomNoteRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public NoteWithLikeDataDTO findNoteWithLikeData(String noteId, String userId) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(noteId)),
                buildNoteWithLikeDataDTOProjectionOperation(userId));
        return mongoOperations.aggregate(aggregation, Note.class, NoteWithLikeDataDTO.class).getUniqueMappedResult();
    }

    @Override
    public List<NoteWithLikeDataDTO> findNotesWithLikeData(NoteQueryDTO noteQuery, String userId) {
        final List<AggregationOperation> operations = new ArrayList<>();

        if(noteQuery.getAuthorId() != null) operations.add(Aggregation.match(new Criteria("authorId").is(noteQuery.getAuthorId())));

        switch (noteQuery.getSortProperty()) {
            case AMOUNT_LIKES -> operations.add(Aggregation.sort(noteQuery.getDirection(), "likeCount"));
            case CREATION_DATE -> operations.add(Aggregation.sort(noteQuery.getDirection(), "creationDate"));
        }

        operations.add(Aggregation.skip(noteQuery.getSkip()));
        operations.add(Aggregation.limit(noteQuery.getLimit()));

        operations.add(buildNoteWithLikeDataDTOProjectionOperation(userId));

        return mongoOperations.aggregate(Aggregation.newAggregation(operations), Note.class, NoteWithLikeDataDTO.class).getMappedResults();
    }

    private ProjectionOperation buildNoteWithLikeDataDTOProjectionOperation(String userId){
        ArrayOperators.ArrayOperatorFactory likes = ArrayOperators.arrayOf(ConditionalOperators.ifNull("likes")
                .then(Collections.emptyList()));
        return Aggregation.project(NoteWithLikeDataDTO.class)
                .and(likes.containsValue(userId != null ? userId : "")).as("likedByCurrentUser")
                .and(likes.length()).as("likeCount");
    }

    @Override
    public void edit(NoteFormDTO note) {
        Query query = new Query(Criteria.where("id").is(note.getId()));
        Update update = new Update().set("text", note.getText()).set("editionDate", new Date());
        mongoOperations.updateFirst(query, update, Note.class);
    }

    @Override
    public void addLike(String noteId, String userId) {
        Query query = new Query(Criteria.where("id").is(noteId));
        Update update = new Update().addToSet("likes", userId);
        mongoOperations.updateFirst(query, update, Note.class);
    }

    @Override
    public void deleteLike(String noteId, String userId) {
        Query query = new Query(Criteria.where("id").is(noteId));
        Update update = new Update().pull("likes", userId);
        mongoOperations.updateFirst(query, update, Note.class);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteNotesAndLikesByAuthorId(String authorId) {
        Update updateDeleteLikes = new Update().pull("likes", authorId);
        mongoOperations.updateMulti(new Query(), updateDeleteLikes, Note.class);

        Query queryDeleteNotes = new Query(Criteria.where("authorId").is(authorId));
        mongoOperations.findAllAndRemove(queryDeleteNotes, Note.class);
    }
}
