package store.aurora.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.order.entity.Wrap;
import store.aurora.order.exception.exception404.WrapNotFoundException;
import store.aurora.order.exception.exception409.WrapNameAlreadyExistsException;
import store.aurora.order.repository.WrapRepository;
import store.aurora.order.service.WrapService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WrapServiceImpl implements WrapService {
    private final WrapRepository wrapRepository;

    @Override
    public boolean isExist(Long id) {
        return wrapRepository.existsById(id);
    }

    @Override
    public void createWrap(Wrap wrap) {
        if(Objects.isNull(wrap)) {
            throw new IllegalArgumentException("wrap is null");
        }
        if(Objects.isNull(wrap.getName())) {
            throw new IllegalArgumentException("wrap name is null");
        }

        wrapRepository.findAll().forEach(w -> {
            if(w.getName().equals(wrap.getName())) {
                throw new WrapNameAlreadyExistsException("wrap name is duplicated");
            }
        });

        wrapRepository.save(wrap);
    }

    @Override
    public Wrap getWrap(Long id) {
        if(!isExist(id)) {
            throw new WrapNotFoundException(id);
        }
        return wrapRepository.getReferenceById(id);
    }

    @Override
    public List<Wrap> getWraps() {
        return wrapRepository.findAll();
    }

    @Override
    @Transactional
    public void updateWrap(Wrap wrap) {
        if(Objects.isNull(wrap)) {
            throw new IllegalArgumentException("wrap is null");
        }
        if(Objects.isNull(wrap.getName())){
            throw new IllegalArgumentException("wrap name is null");
        }
        if(Objects.isNull(wrap.getId())) {
            throw new IllegalArgumentException("wrap id is null");
        }
        else if(!isExist(wrap.getId())) {
            throw new WrapNotFoundException(wrap.getId());
        }
        wrapRepository.save(wrap);
    }

    @Override
    public void deleteByWrapId(Long wrapId) {
        if(Objects.isNull(wrapId)) {
            throw new IllegalArgumentException("wrap id is null");
        }
        if(!isExist(wrapId)) {
            throw new WrapNotFoundException(wrapId);
        }
        wrapRepository.deleteById(wrapId);
    }
}
