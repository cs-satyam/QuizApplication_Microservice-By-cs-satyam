package com.cs_satyam.quizservice.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import com.cs_satyam.quizservice.model.Quiz;

public interface QuizDao extends JpaRepository<Quiz,Integer> {
}
