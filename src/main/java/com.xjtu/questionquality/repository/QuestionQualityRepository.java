package com.xjtu.questionquality.repository;

import com.xjtu.questionquality.domain.QuestionAssemble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuestionQualityRepository extends JpaRepository<QuestionAssemble,Long>,JpaSpecificationExecutor<QuestionAssemble>{

}
