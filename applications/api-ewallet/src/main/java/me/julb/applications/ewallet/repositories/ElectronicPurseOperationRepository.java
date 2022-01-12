/**
 * MIT License
 *
 * Copyright (c) 2017-2021 Julb
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

package me.julb.applications.ewallet.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import me.julb.applications.ewallet.entities.ElectronicPurseOperationEntity;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationType;
import me.julb.springbootstarter.persistence.mongodb.reactive.repositories.MongoSpecificationExecutor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The electronic purse operation repository.
 * <br>
 * @author Julb.
 */
public interface ElectronicPurseOperationRepository extends ReactiveMongoRepository<ElectronicPurseOperationEntity, String>, MongoSpecificationExecutor<ElectronicPurseOperationEntity> {
    /**
     * Finds electronic purse operations by its trademark and electronic purse id.
     * @param tm the trademark.
     * @param electronicPurseId the electronic purse ID.
     * @return the electronic purse operation items.
     */
    Flux<ElectronicPurseOperationEntity> findByTmAndElectronicPurseId(String tm, String electronicPurseId);

    /**
     * Finds an electronic purse operation by its trademark, electronic purse id and id.
     * @param tm the trademark.
     * @param electronicPurseId the electronic purse ID.
     * @param id the id.
     * @return the electronic purse operation entity, <code>null</code> otherwise.
     */
    Mono<ElectronicPurseOperationEntity> findByTmAndElectronicPurseIdAndId(String tm, String electronicPurseId, String id);

    /**
     * Finds an electronic purse operation by its trademark, electronic purse, type and original operation id.
     * @param tm the trademark.
     * @param electronicPurseId the electronic purse ID.
     * @param operationType the operation type.
     * @param originalOperation the original operation.
     * @return the electronic purse operation entity, <code>null</code> otherwise.
     */
    Mono<ElectronicPurseOperationEntity> findByTmAndElectronicPurseIdAndTypeAndOriginalOperation(String tm, String electronicPurseId, ElectronicPurseOperationType operationType, ElectronicPurseOperationEntity originalOperation);
}
