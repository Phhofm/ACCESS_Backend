package ch.uzh.ifi.access.course.dao;

import ch.uzh.ifi.access.course.model.workspace.StudentSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubmissionRepository extends MongoRepository<StudentSubmission, String>, CustomizedStudentSubmissionRepository {

    <T extends StudentSubmission> List<T> findAllByExerciseIdAndUserIdOrderByVersionDesc(String exerciseId, String userId);

    <T extends StudentSubmission> Optional<T> findTopByExerciseIdAndUserIdOrderByVersionDesc(String exerciseId, String userId);
}
