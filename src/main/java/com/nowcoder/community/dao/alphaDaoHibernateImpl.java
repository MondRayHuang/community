package com.nowcoder.community.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

@Repository("alphaHibernate")
public class alphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
