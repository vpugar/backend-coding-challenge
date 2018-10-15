package com.engagetech.expenses.repository;

import com.engagetech.expenses.model.Expense;
import com.engagetech.expenses.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface ExpenseRepository extends CrudRepository<Expense, Long> {

    Stream<Expense> findAllByUserOrderByDateAscCreatedAtAsc(User user);

}
