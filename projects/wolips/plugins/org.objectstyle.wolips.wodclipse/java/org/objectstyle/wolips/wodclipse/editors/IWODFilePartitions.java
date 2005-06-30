package org.objectstyle.wolips.wodclipse.editors;

public interface IWODFilePartitions {
  public static final String WOD_FILE_PARTITIONING = "___wf_partitioning"; //$NON-NLS-1$

  public static final String ASSOCIATION_NAME = "__wf_association_name"; //$NON-NLS-1$
  public static final String ASSOCIATION_VALUE = "__wf_association_value"; //$NON-NLS-1$
  public static final String CONSTANT_ASSOCIATION_VALUE = "__wf_constant_association_value"; //$NON-NLS-1$
  public static final String PARENT_ASSOCIATION_VALUE = "__wf_parent_association_value"; //$NON-NLS-1$

  public static final String COMPONENT_NAME = "__wf_component_name"; //$NON-NLS-1$
  public static final String COMPONENT_TYPE = "__wf_component_type"; //$NON-NLS-1$
  
  public static final String OPERATOR = "__wf_operator"; //$NON-NLS-1$

  public static final String COMMENT = "__wf_comment"; //$NON-NLS-1$
  public static final String DEFINITION = "__wf_definition"; //$NON-NLS-1$
  public static final String[] PARTITIONS = new String[] { IWODFilePartitions.COMMENT, IWODFilePartitions.DEFINITION };
  //public static final String[] PARTITIONS = new String[] { IWODFilePartitions.COMMENT, ASSOCIATION_NAME, ASSOCIATION_VALUE, CONSTANT_ASSOCIATION_VALUE, COMPONENT_NAME, COMPONENT_TYPE, OPERATOR };
}
