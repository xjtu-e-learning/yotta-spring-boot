package com.xjtu.subject.repository;


import com.xjtu.subject.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SubjectRepository extends JpaRepository<Subject, Long>, JpaSpecificationExecutor<Subject> {
    /**
     * 根据学科id,查询学科
     *
     * @param subjectId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Subject findBySubjectId(Long subjectId);

    /**
     * 根据学科id和学科名,查询学科
     *
     * @param subjectId
     * @param subjectName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Subject findBySubjectIdAndSubjectName(Long subjectId, String subjectName);

    @Transactional(rollbackFor = Exception.class)
    Subject findBySubjectName(String subjectName);

    /**
     * 根据学科id，更新学科
     *
     * @param subjectId
     * @param newSubjectId
     * @param subjectName
     * @param note
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("update Subject s set s.subjectId = ?2, s.subjectName = ?3, s.note = ?4 where s.subjectId = ?1")
    void updateBySubjectId(Long subjectId, Long newSubjectId, String subjectName, String note);

}
