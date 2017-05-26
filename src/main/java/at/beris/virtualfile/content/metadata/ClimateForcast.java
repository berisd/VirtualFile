/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.beris.virtualfile.content.metadata;

/**
 * Met keys from NCAR CCSM files in the <a
 * href="http://cf-pcmdi.llnl.gov/">Climate Forecast Convention</a>.
 */
public interface ClimateForcast {

    public static final String PROGRAM_ID = "prg_ID";

    public static final String COMMAND_LINE = "cmd_ln";

    public static final String HISTORY = "history";

    public static final String TABLE_ID = "table_id";

    public static final String INSTITUTION = "institution";

    public static final String SOURCE = "source";

    public static final String CONTACT = "contact";

    public static final String PROJECT_ID = "project_id";

    public static final String CONVENTIONS = "Conventions";

    public static final String REFERENCES = "references";

    public static final String ACKNOWLEDGEMENT = "acknowledgement";

    public static final String REALIZATION = "realization";

    public static final String EXPERIMENT_ID = "experiment_id";

    public static final String COMMENT = "comment";

    public static final String MODEL_NAME_ENGLISH = "model_name_english";

}
