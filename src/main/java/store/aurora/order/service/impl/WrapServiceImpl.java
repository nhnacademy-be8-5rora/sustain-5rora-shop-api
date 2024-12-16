package store.aurora.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.order.entity.Wrap;
import store.aurora.order.repository.WrapRepository;
import store.aurora.order.service.WrapService;

@Service
@RequiredArgsConstructor
public class WrapServiceImpl implements WrapService {
    private final WrapRepository wrapRepository;
    @Override
    public Wrap createWrap(Wrap wrap) {
        return wrapRepository.save(wrap);
    }

    @Override
    public Wrap getWrap(Long id) {
        return wrapRepository.getReferenceById(id);
    }

    @Override
    public void updateWrap(Wrap wrap) {
        wrapRepository.save(wrap);
    }

    @Override
    public void deleteWrap(Wrap wrap) {
        wrapRepository.delete(wrap);
    }

    @Override
    public void deleteByWrapId(Long wrapId) {
        wrapRepository.deleteById(wrapId);
    }
}
