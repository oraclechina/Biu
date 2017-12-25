/**
 * Copyright (c) 2014-2017, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.cloud.biu.modules.biubiu.ssh;

import java.util.logging.Level;

import com.oracle.cloud.biu.entity.ComputeEntity;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

/**
 * Jsch Logger.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id: c80100320b243220b2bc05f4a8bbb99e576d486b $
 * @since 1.4
 */
@ToString
@EqualsAndHashCode
@Log4j
final class JschLogger implements com.jcraft.jsch.Logger {

    @Override
    public boolean isEnabled(final int level) {
        return level >= com.jcraft.jsch.Logger.WARN;
    }

    @Override
    public void log(final int level, final String msg) {
        final Level jul;
        if (level >= com.jcraft.jsch.Logger.ERROR) {
            jul = Level.SEVERE;
        } else if (level >= com.jcraft.jsch.Logger.WARN) {
            jul = Level.WARNING;
        } else {
            jul = Level.INFO;
        }
        log.debug(msg);
    }

}
