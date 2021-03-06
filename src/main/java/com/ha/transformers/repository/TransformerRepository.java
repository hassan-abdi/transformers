package com.ha.transformers.repository;

import com.ha.transformers.domain.Transformer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransformerRepository extends CrudRepository<Transformer, Long> {
    @Override
    List<Transformer> findAll();
}
