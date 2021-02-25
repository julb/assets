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

package me.julb.applications.authorizationserver.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.mail.UserMailEntity;
import me.julb.applications.authorizationserver.entities.mobilephone.UserMobilePhoneEntity;
import me.julb.applications.authorizationserver.repositories.UserMailRepository;
import me.julb.applications.authorizationserver.repositories.UserMobilePhoneRepository;
import me.julb.applications.authorizationserver.repositories.UserRepository;
import me.julb.applications.authorizationserver.services.UserAccountRecoveryService;
import me.julb.applications.authorizationserver.services.dto.recovery.RecoveryChannelDeviceDTO;
import me.julb.applications.authorizationserver.services.dto.recovery.RecoveryChannelType;
import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.configs.ConfigSourceService;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;

/**
 * The user account recovery service.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserAccountRecoveryServiceImpl implements UserAccountRecoveryService {

    /**
     * The user repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The user mail repository.
     */
    @Autowired
    private UserMailRepository userMailRepository;

    /**
     * The item repository.
     */
    @Autowired
    private UserMobilePhoneRepository userMobilePhoneRepository;

    /**
     * The config source service.
     */
    @Autowired
    private ConfigSourceService configSourceService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecoveryChannelDeviceDTO> findAll(@NotNull @Identifier String userId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Assert the recovery channel is supported.
        RecoveryChannelType[] supportedRecoveryChannelTypes = configSourceService.getTypedProperty("authorization-server.account-recovery.channel-types", RecoveryChannelType[].class);

        // Result list
        List<RecoveryChannelDeviceDTO> channelDeviceTypes = new ArrayList<>();

        // Channel mail.
        if (ArrayUtils.contains(supportedRecoveryChannelTypes, RecoveryChannelType.MAIL)) {
            List<UserMailEntity> verifiedUserMails = userMailRepository.findByTmAndUser_IdAndVerifiedIsTrue(tm, userId);
            for (UserMailEntity userMail : verifiedUserMails) {
                String maskedMail = maskMailAddress(userMail.getMail());
                channelDeviceTypes.add(new RecoveryChannelDeviceDTO(userMail.getId(), RecoveryChannelType.MAIL, maskedMail));
            }
        }

        // Channel mobile phones.
        if (ArrayUtils.contains(supportedRecoveryChannelTypes, RecoveryChannelType.MOBILE_PHONE)) {
            List<UserMobilePhoneEntity> verifiedMobilePhones = userMobilePhoneRepository.findByTmAndUser_IdAndVerifiedIsTrue(tm, userId);
            for (UserMobilePhoneEntity userMobilePhone : verifiedMobilePhones) {
                String maskedMobilePhone = maskMobilePhoneNumber(userMobilePhone.getMobilePhone().getE164Number());
                channelDeviceTypes.add(new RecoveryChannelDeviceDTO(userMobilePhone.getId(), RecoveryChannelType.MOBILE_PHONE, maskedMobilePhone));
            }
        }

        // Sort result
        Collections.sort(channelDeviceTypes);

        // Return result.
        return channelDeviceTypes;
    }

    // ------------------------------------------ Write methods.

    // ------------------------------------------ Private methods.

    /**
     * Masks the given mail address.
     * @param mailAddress the mail address.
     * @return the masked mail address.
     */
    protected String maskMailAddress(String mailAddress) {
        String[] mailAddressParts = StringUtils.split(mailAddress, Chars.AT);

        StringBuffer sb = new StringBuffer();
        sb.append(mailAddressParts[0].substring(Integers.ZERO, Integers.ONE));
        sb.append(StringUtils.repeat(Chars.STAR, Integers.TEN));
        sb.append(mailAddressParts[0].substring(mailAddressParts[0].length() - Integers.ONE));
        sb.append(Chars.AT);
        sb.append(mailAddressParts[1]);
        return sb.toString();
    }

    /**
     * Masks the given mobile phone number.
     * @param mobilePhoneNumber the mobile phone number.
     * @return the masked mobile phone number.
     */
    protected String maskMobilePhoneNumber(String mobilePhoneNumber) {
        StringBuffer sb = new StringBuffer();
        sb.append(mobilePhoneNumber.substring(Integers.ZERO, Integers.FOUR));
        sb.append(StringUtils.repeat(Chars.STAR, Integers.TEN));
        sb.append(mobilePhoneNumber.substring(mobilePhoneNumber.length() - Integers.TWO));
        return sb.toString();
    }
}
