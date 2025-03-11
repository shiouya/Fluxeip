package com.example.fluxeip.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.model.Type;
import com.example.fluxeip.repository.TypeRepository;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class TypeService {

    @Autowired
    private TypeRepository typeRepository;

    public List<Type> getAllTypes() {
        return typeRepository.findAll();
    }

    public List<Type> getTypesByCategory(String category) {
        return typeRepository.findByCategory(category);
    }

    public Optional<Type> getTypeById(Integer id) {
        return typeRepository.findById(id);
    }

    public Type createType(Type type) {
        return typeRepository.save(type);
    }

    public Type updateType(Integer id, Type updatedType) {
        return typeRepository.findById(id).map(type -> {
            type.setTypeName(updatedType.getTypeName());
            type.setCategory(updatedType.getCategory());
            return typeRepository.save(type);
        }).orElseThrow(() -> new RuntimeException("Type not found"));
    }

    public void deleteType(Integer id) {
        typeRepository.deleteById(id);
    }
}
