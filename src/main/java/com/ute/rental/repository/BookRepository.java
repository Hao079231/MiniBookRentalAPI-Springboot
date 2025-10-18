package com.ute.rental.repository;

import com.ute.rental.model.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

  Optional<Book> findByTitleAndAuthor(String title, String author);

  boolean existsByTitleAndAuthor(String title, String author);

  @Modifying
  @Query("UPDATE Book b SET b.category = NULL WHERE b.category.id = :categoryId")
  void removeCategoryId(@Param("categoryId") Long categoryId);

  @Modifying
  @Query("UPDATE Book b SET b.stock = b.stock + :count WHERE b.id = :bookId")
  void updateStock(@Param("bookId") Long bookId, @Param("count") Integer count);

}
