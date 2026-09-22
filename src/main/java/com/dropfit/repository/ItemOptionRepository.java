package com.dropfit.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.dropfit.domain.item.ItemOption;

@Repository
@RequiredArgsConstructor
public class ItemOptionRepository {

    private final EntityManager em;

    public ItemOption findItemOption(Long itemOptionId) {
        return em.find(ItemOption.class, itemOptionId);
    }
 }

