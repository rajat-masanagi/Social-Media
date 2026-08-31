package com.example.textsocial.social.repository;

import com.example.textsocial.social.domain.Content;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, UUID> {}
