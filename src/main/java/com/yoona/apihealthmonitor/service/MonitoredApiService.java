package com.yoona.apihealthmonitor.service;

import com.yoona.apihealthmonitor.dto.MonitoredApiCreateRequest;
import com.yoona.apihealthmonitor.dto.MonitoredApiResponse;
import com.yoona.apihealthmonitor.dto.MonitoredApiUpdateRequest;
import com.yoona.apihealthmonitor.entity.MonitoredApi;
import com.yoona.apihealthmonitor.exception.DuplicateResourceException;
import com.yoona.apihealthmonitor.exception.ResourceNotFoundException;
import com.yoona.apihealthmonitor.repository.MonitoredApiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MonitoredApiService {
    private final MonitoredApiRepository repository;

    public MonitoredApiService(MonitoredApiRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MonitoredApiResponse create(MonitoredApiCreateRequest request) {
        if (repository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("이미 같은 이름의 API가 등록되어 있습니다.");
        }
        MonitoredApi api = new MonitoredApi();
        api.setName(request.name().trim());
        api.setEndpointUrl(request.endpointUrl().trim());
        api.setHttpMethod(request.httpMethod().toUpperCase());
        api.setActive(request.active() == null || request.active());
        return toResponse(repository.save(api));
    }

    public List<MonitoredApiResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public MonitoredApiResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional
    public MonitoredApiResponse update(Long id, MonitoredApiUpdateRequest request) {
        MonitoredApi api = getEntity(id);
        api.setName(request.name().trim());
        api.setEndpointUrl(request.endpointUrl().trim());
        api.setHttpMethod(request.httpMethod().toUpperCase());
        api.setActive(request.active());
        return toResponse(api);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    public MonitoredApi getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("API를 찾을 수 없습니다. id=" + id));
    }

    private MonitoredApiResponse toResponse(MonitoredApi api) {
        return new MonitoredApiResponse(api.getId(), api.getName(), api.getEndpointUrl(), api.getHttpMethod(),
                api.isActive(), api.getCreatedAt(), api.getUpdatedAt());
    }
}
