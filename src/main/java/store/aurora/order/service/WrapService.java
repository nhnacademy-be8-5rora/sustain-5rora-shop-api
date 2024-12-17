package store.aurora.order.service;

import store.aurora.order.entity.Wrap;

public interface WrapService {
    Wrap createWrap(Wrap wrap);
    Wrap getWrap(Long id);
    void updateWrap(Wrap wrap);
    void deleteWrap(Wrap wrap);
    void deleteByWrapId(Long wrapId);
}