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

package me.julb.applications.authorizationserver.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import me.julb.applications.authorizationserver.entities.mobilephone.UserMobilePhoneEntity;
import me.julb.springbootstarter.persistence.mongodb.reactive.repositories.MongoSpecificationExecutor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The user mobile phone repository.
 * <br>
 * @author Julb.
 */
public interface UserMobilePhoneRepository extends ReactiveMongoRepository<UserMobilePhoneEntity, String>, MongoSpecificationExecutor<UserMobilePhoneEntity> {

    /**
     * Finds an user primary mobile phone by trademark and user ID.
     * @param tm the trademark.
     * @param userId the user ID.
     * @return the user mail, or <code>null</code> if not exists.
     */
    Mono<UserMobilePhoneEntity> findByTmAndUser_IdAndPrimaryIsTrue(String tm, String userId);

    /**
     * Finds verified user mobile phones by trademark and id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @return the verified user mobile phones, or <code>null</code> if not exists.
     */
    Flux<UserMobilePhoneEntity> findByTmAndUser_IdAndVerifiedIsTrue(String tm, String userId);

    /**
     * Finds an user mobile phone by trademark and id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param id the id.
     * @return the user mobile phone, or <code>null</code> if not exists.
     */
    Mono<UserMobilePhoneEntity> findByTmAndUser_IdAndId(String tm, String userId, String id);

    /**
     * Finds an user mobile phone by trademark, country code and number address.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param countryCode the country code.
     * @param number the number.
     * @return <code>true</code> if the mail exists, <code>false</code> otherwise.
     */
    Mono<Boolean> existsByTmAndUser_IdAndMobilePhone_CountryCodeIgnoreCaseAndMobilePhone_NumberIgnoreCase(String tm, String userId, String countryCode, String number);

    /**
     * Finds an user mobile phone by trademark and id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param id the id.
     * @return the user mobile phone, or <code>null</code> if not exists.
     */
    Mono<UserMobilePhoneEntity> findByTmAndUser_IdAndIdAndVerifiedIsTrue(String tm, String userId, String id);
}
