package com.vakya.post_service.repository;

import com.vakya.post_service.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Post findByIdIs(Long id);

    @Query("SELECT DISTINCT p FROM posts p JOIN p.tagList t " +
            "WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND p.author IN :authors AND t.name IN :tags")
    Page<Post> findByQueryAndAuthorsAndTags(@Param("query") String query,
                                            @Param("authors") List<String> authors,
                                            @Param("tags") List<String> tags,
                                            Pageable pageable);

    @Query("SELECT DISTINCT p FROM posts p JOIN p.tagList t " +
            "WHERE p.author IN :authors AND t.name IN :tags")
    Page<Post> findByAuthorsAndTags(@Param("authors") List<String> authors,
                                    @Param("tags") List<String> tags,
                                    Pageable pageable);

    @Query("SELECT DISTINCT p.author FROM posts p")
    List<String> findDistinctAuthors();

    @Query("SELECT DISTINCT t.name FROM tags t")
    List<String> findDistinctTagNames();

    @Query("SELECT DISTINCT p FROM posts p " +
            "WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND p.author IN :authors")
    Page<Post> findByQueryAndAuthors(@Param("query") String query,
                                     @Param("authors") List<String> authors,
                                     Pageable pageable);

    @Query("SELECT DISTINCT p FROM posts p JOIN p.tagList t " +
            "WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND t.name IN :tags")
    Page<Post> findByQueryAndTags(@Param("query") String query,
                                  @Param("tags") List<String> tags,
                                  Pageable pageable);

    @Query("SELECT DISTINCT p FROM posts p LEFT JOIN p.tagList t " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Post> searchPosts(@Param("query") String query, Pageable pageable);

    Page<Post> findByAuthorIn(List<String> authors, Pageable pageable);


    @Query("SELECT DISTINCT p FROM posts p JOIN p.tagList t WHERE LOWER(t.name) IN :tags")
    Page<Post> findByTagsIn(@Param("tags") List<String> tags, Pageable pageable);

}
