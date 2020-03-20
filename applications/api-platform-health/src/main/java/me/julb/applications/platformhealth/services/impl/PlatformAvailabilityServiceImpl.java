/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.julb.applications.platformhealth.services.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.platformhealth.entities.ComponentCategoryEntity;
import me.julb.applications.platformhealth.entities.ComponentEntity;
import me.julb.applications.platformhealth.entities.IncidentComponentEntity;
import me.julb.applications.platformhealth.entities.IncidentEntity;
import me.julb.applications.platformhealth.entities.PlannedMaintenanceComponentEntity;
import me.julb.applications.platformhealth.entities.PlannedMaintenanceEntity;
import me.julb.applications.platformhealth.repositories.ComponentCategoryRepository;
import me.julb.applications.platformhealth.repositories.ComponentRepository;
import me.julb.applications.platformhealth.repositories.IncidentComponentRepository;
import me.julb.applications.platformhealth.repositories.IncidentRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceComponentRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceRepository;
import me.julb.applications.platformhealth.services.PlatformAvailabilityService;
import me.julb.applications.platformhealth.services.dto.availability.AvailabilityStatus;
import me.julb.applications.platformhealth.services.dto.availability.ComponentAvailabilityDTO;
import me.julb.applications.platformhealth.services.dto.availability.ComponentCategoryAvailabilityHierarchyDTO;
import me.julb.applications.platformhealth.services.dto.availability.PlatformAvailabilityDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentStatus;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceStatus;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.mapping.services.IMappingService;
import me.julb.springbootstarter.persistence.mongodb.specifications.AttributeInIdentifiableSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;

/**
 * The platform availability service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class PlatformAvailabilityServiceImpl implements PlatformAvailabilityService {

    /**
     * The incident repository.
     */
    @Autowired
    private IncidentRepository incidentRepository;

    /**
     * The incident component repository.
     */
    @Autowired
    private IncidentComponentRepository incidentComponentRepository;

    /**
     * The planned maintenance repository.
     */
    @Autowired
    private PlannedMaintenanceRepository plannedMaintenanceRepository;

    /**
     * The planned maintenance component repository.
     */
    @Autowired
    private PlannedMaintenanceComponentRepository plannedMaintenanceComponentRepository;

    /**
     * The component category repository.
     */
    @Autowired
    private ComponentCategoryRepository componentCategoryRepository;

    /**
     * The component repository.
     */
    @Autowired
    private ComponentRepository componentRepository;

    /**
     * The component mapper.
     */
    @Autowired
    private IMappingService mappingService;

    /**
     * {@inheritDoc}
     */
    @Override
    public PlatformAvailabilityDTO getPlatformAvailability() {
        String tm = TrademarkContextHolder.getTrademark();

        // Gets all the data.
        List<ComponentCategoryEntity> componentCategories = componentCategoryRepository.findByTmOrderByPositionAsc(tm);
        List<ComponentEntity> components = componentRepository.findByTmOrderByPositionAsc(tm);
        List<IncidentEntity> incidentsInProgress = incidentRepository.findByTmAndStatusIn(tm, IncidentStatus.inProgressStatuses());
        List<PlannedMaintenanceEntity> plannedMaintenancesInProgress = plannedMaintenanceRepository.findByTmAndStatusIn(tm, PlannedMaintenanceStatus.inProgressStatuses());

        // Get links between incidents and components.
        List<IncidentComponentEntity> incidentComponentsInProgress = incidentComponentRepository.findAll(new TmSpecification<IncidentComponentEntity>(tm).and(new AttributeInIdentifiableSpecification<>("incident", incidentsInProgress)));

        // Get links between incidents and components.
        List<PlannedMaintenanceComponentEntity> plannedMaintenanceComponentsInProgress =
            plannedMaintenanceComponentRepository.findAll(new TmSpecification<PlannedMaintenanceComponentEntity>(tm).and(new AttributeInIdentifiableSpecification<>("plannedMaintenance", plannedMaintenancesInProgress)));

        // Build tree.
        PlatformAvailabilityDTO availabilityDTO = new PlatformAvailabilityDTO();
        availabilityDTO.setIncidentsInProgressCount(Long.valueOf(incidentsInProgress.size()));
        availabilityDTO.setPlannedMaintenancesInProgressCount(Long.valueOf(plannedMaintenancesInProgress.size()));

        for (ComponentCategoryEntity componentCategory : componentCategories) {
            // Map category to DTO.
            ComponentCategoryAvailabilityHierarchyDTO componentCategoryAvailability = mappingService.map(componentCategory, ComponentCategoryAvailabilityHierarchyDTO.class);

            // Availability of the category.
            AvailabilityStatus categoryAvailability = AvailabilityStatus.UP;

            // Add sub components to the category.
            for (ComponentEntity component : components) {
                if (component.getComponentCategory().equals(componentCategory)) {
                    // Map component to availability
                    ComponentAvailabilityDTO componentAvailability = mappingService.map(component, ComponentAvailabilityDTO.class);

                    // Get numbers of incidents.
                    componentAvailability.setIncidentsInProgressCount(countIncidentsInProgressPerComponent(incidentComponentsInProgress, component));

                    // Get number of planned maintenances
                    componentAvailability.setPlannedMaintenancesInProgressCount(countPlannedMaintenancesInProgressPerComponent(plannedMaintenanceComponentsInProgress, component));

                    // Compute status of the component.
                    componentAvailability.setAvailabilityStatus(getCurrentComponentStatus(incidentComponentsInProgress, component));

                    // Add component to category.
                    componentCategoryAvailability.getComponentsAvailability().add(componentAvailability);

                    // Compute availability status of the category - take the worsest.
                    categoryAvailability = AvailabilityStatus.worsest(categoryAvailability, componentAvailability.getAvailabilityStatus());
                }
            }

            // Get numbers of incidents.
            componentCategoryAvailability.setIncidentsInProgressCount(countIncidentsInProgressPerComponentCategory(incidentComponentsInProgress, componentCategory));

            // Get number of planned maintenances
            componentCategoryAvailability.setPlannedMaintenancesInProgressCount(countPlannedMaintenancesInProgressPerComponentCategory(plannedMaintenanceComponentsInProgress, componentCategory));

            // Set status
            componentCategoryAvailability.setAvailabilityStatus(categoryAvailability);

            // Add category to the list.
            availabilityDTO.getComponentCategoriesAvailability().add(componentCategoryAvailability);
        }

        // availabilityDTO.setGlobalAvailability(globalAvailability);
        return availabilityDTO;
    }

    /**
     * Gets the number of incidents in progress for the given component.
     * @param incidentsInProgress the incidents in progress.
     * @param component the component.
     * @return the number of incidents in progress.
     */
    private Long countIncidentsInProgressPerComponent(Collection<IncidentComponentEntity> incidentsInProgress, ComponentEntity component) {
        return incidentsInProgress.stream().filter((incidentComponent) -> {
            return incidentComponent.getComponent().equals(component);
        }).count();
    }

    /**
     * Gets the status of the given component.
     * @param incidentsInProgress the incidents in progress.
     * @param component the component.
     * @return the number of incidents in progress.
     */
    private AvailabilityStatus getCurrentComponentStatus(Collection<IncidentComponentEntity> incidentsInProgress, ComponentEntity component) {
        Collection<AvailabilityStatus> availabilityStatuses = incidentsInProgress.stream().filter((incidentComponent) -> {
            return incidentComponent.getComponent().equals(component);
        }).map((incidentComponent) -> {
            switch (incidentComponent.getImpactLevel()) {
                case PARTIAL:
                    return AvailabilityStatus.PARTIAL;
                case DOWN:
                    return AvailabilityStatus.DOWN;
                default:
                    throw new UnsupportedOperationException();
            }
        }).collect(Collectors.toSet());

        return AvailabilityStatus.worsest(availabilityStatuses.toArray(new AvailabilityStatus[0]));
    }

    /**
     * Gets the number of planned maintenances in progress for the given component.
     * @param plannedMaintenancesInProgress the planned maintenances in progress.
     * @param component the component.
     * @return the number of planned maintenances in progress.
     */
    private Long countPlannedMaintenancesInProgressPerComponent(Collection<PlannedMaintenanceComponentEntity> plannedMaintenancesInProgress, ComponentEntity component) {
        return plannedMaintenancesInProgress.stream().filter((plannedMaintenanceComponent) -> {
            return plannedMaintenanceComponent.getComponent().equals(component);
        }).count();
    }

    /**
     * Gets the number of incidents in progress for the given component category.
     * @param incidentsInProgress the incidents in progress.
     * @param componentCategory the component category.
     * @return the number of incidents in progress.
     */
    private Long countIncidentsInProgressPerComponentCategory(Collection<IncidentComponentEntity> incidentsInProgress, ComponentCategoryEntity componentCategory) {
        return incidentsInProgress.stream().filter((incidentComponent) -> {
            return incidentComponent.getComponent().getComponentCategory().equals(componentCategory);
        }).count();
    }

    /**
     * Gets the number of planned maintenances in progress for the given component category.
     * @param plannedMaintenancesInProgress the planned maintenances in progress.
     * @param componentCategory the component category.
     * @return the number of planned maintenances in progress.
     */
    private Long countPlannedMaintenancesInProgressPerComponentCategory(Collection<PlannedMaintenanceComponentEntity> plannedMaintenancesInProgress, ComponentCategoryEntity componentCategory) {
        return plannedMaintenancesInProgress.stream().filter((plannedMaintenanceComponent) -> {
            return plannedMaintenanceComponent.getComponent().getComponentCategory().equals(componentCategory);
        }).count();
    }
}
