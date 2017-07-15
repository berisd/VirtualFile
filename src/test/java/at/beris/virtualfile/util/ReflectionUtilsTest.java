/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import at.beris.virtualfile.provider.AbstractFileOperationProvider;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ReflectionUtilsTest {
    @Test
    public void getClassesForPackage() throws Exception {
        List<Class> classList = ReflectionUtils.getClassesForPackage(AbstractFileOperationProvider.class.getPackage());
        Assert.assertTrue(classList.size() > 0);
    }

    @Test
    public void getSubClassesOfClass() throws Exception {
        List<Class> subClassList = ReflectionUtils.findSubClassesOfClassInPackage(AbstractFileOperationProvider.class);
        Assert.assertTrue(subClassList.size() > 0);
    }

}