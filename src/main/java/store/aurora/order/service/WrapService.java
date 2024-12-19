package store.aurora.order.service;

import store.aurora.order.entity.Wrap;

import java.util.List;

public interface WrapService {
    boolean isExist(Long id);
    void createWrap(Wrap wrap);
    Wrap getWrap(Long id);
    List<Wrap> getWraps();
    void updateWrap(Wrap wrap);
    void deleteByWrapId(Long wrapId);
}