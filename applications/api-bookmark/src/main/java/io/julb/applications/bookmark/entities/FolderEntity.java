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

package io.julb.applications.bookmark.entities;

import io.julb.applications.bookmark.services.dto.ItemType;
import io.julb.applications.bookmark.services.dto.folder.FolderCreationDTO;
import io.julb.applications.bookmark.services.dto.folder.FolderDTO;
import io.julb.applications.bookmark.services.dto.folder.FolderPatchDTO;
import io.julb.applications.bookmark.services.dto.folder.FolderUpdateDTO;
import io.julb.library.dto.simple.identifier.IdentifierDTO;
import io.julb.library.mapping.annotations.ObjectMappingFactory;
import io.julb.library.utility.interfaces.IIdentifiable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The folder entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = FolderCreationDTO.class, patch = FolderPatchDTO.class, read = {FolderDTO.class, IdentifierDTO.class}, update = FolderUpdateDTO.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Document("bookmarks")
public class FolderEntity extends AbstractItemEntity implements IIdentifiable {

    /**
     * Default constructor.
     */
    public FolderEntity() {
        super();
        this.setType(ItemType.FOLDER);
    }
}
