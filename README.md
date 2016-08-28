ssascaling (Self-aware and Self-Adaptive Auto-scaling for Service-based Software Services in the Elastic Cloud)
==========


This is the main repository for ssascaling (Self-aware and Self-Adaptive Auto-scaling for Service-based Software Services in the Elastic Cloud), a framework that exploits the advances of self-awareness principles and search-based software engineering techniques to tackle modelings, architecting and decision making problems for runtime autoscaling in the cloud.

Currently it encapsulates the following sub-frameworks/components:

###SAM: Self-Adaptive Modeling###

This is a framework that enables self-adaptive feature selection and selection of learning algorithms to model the correlation of control features, environments, interference to Quality of Service (QoS) attributes (e.g., response time). More details can be found in the following publications:

 > * T. Chen and R. Bahsoon , Self-Adaptive and Online QoS Modeling for Cloud-Based Software Services. IEEE Transactions on Software Engineering, to appear.

 > * T. Chen, R. Bahsoon and X. Yao. Online QoS Modeling in the Cloud: A Hybrid and Adaptive Multi-Learners Approach. The 7th IEEE/ACM International Conference on Utility and Cloud Computing (UCC2014), London, UK. 2014.

 > * T. Chen and R. Bahsoon. Self-Adaptive and Sensitivity-Aware QoS Modelling for the Cloud. The 8th International Symposium on Software Engineering for Adaptive and Self-Managing Systems, SEAMS in conjunction with the 35th International Conference on Software Engineering (ICSE), San Francisco, CA, 2013.



###RCA : Region Controlled Architecture###

This is a component that intelligent partitions the architecture with respect to objective-dependency, which is determined by the inputs of QoS model.  More details can be found in the following publications:

 > * T. Chen and R. Bahsoon. Symbiotic and Sensitivity-Aware Architecture for Globally-Optimal Benefit in Self-Adaptive Cloud. The 9th International Symposium on Software Engineering for Adaptive and Self-Managing Systems, SEAMS in conjunction with the 36th International Conference on Software Engineering (ICSE), India, 2014.

 > * T. Chen, R. Bahsoon and G. Theodoropoulos. A Decentralized Architecture for Dynamic QoS Optimization in Cloud-based DDDAS. 2013 International Conference on Computational Science, Procedia of Computer Science, Elsevier Science, 2013.




### MOACO: Self-Adaptive and Interference-Aware Multi-Objective Ant Colony Optimization for Decision Making in Self-Adaptive Software 

This is a component that exploits multi-objective ant colony algorithm to optimise autoscaling decisions for cloud configuration and provisioning. It particularly considers QoS interference caused by multi-tenants and virtualized environment, e.g. cloud computing. The approach leverage nash dominance, a popular economic principle, to find well-compromised/knee trade-off decisions. More details can be found in the following publications:

  > * T. Chen and R. Bahsoon , Self-Adaptive Trade-off Decision Making for Autoscaling Cloud-Based Services. IEEE Transactions on Services Computing, 2015, doi:10.1109/TSC.2015.2499770.

- - - -

The project has been recreated as [SSASE framework](https://github.com/taochen/ssase), thus this repository will not be updated in the near future. New developments and integration would be carry on with the new framework.

