#!/usr/bin/env python
from pyhocon import ConfigFactory


class Prop:
    """
    properties declaration
    """
    def __init__(self, config_path):
        """ default constructor """
        conf = ConfigFactory.parse_file(config_path)

        # number of topic
        self.num_topic = conf['num_topic']
        # alpha
        self.alpha = conf['alpha']
        # beta
        self.beta = conf['beta']
        # num_keywords
        self.num_keywords = conf['num_keywords']
        # num_threads
        self.num_thread = conf['num_thread']
        # iteration
        self.iter = conf['iter']
        # seed
        self.seed = conf['seed']

    def getNumTopic(self):
        """ get the number of topic """
        return self.num_topic

    def getNumKeywords(self):
        """ get the number of keywords """
        return self.num_keywords

    def getIteration(self):
        """ get the number of iteration """
        return self.iter


def main():
    """ main driver program """
    props = Prop('../config/topinion.conf')
    print "number of topic: " + str(props.num_topic)
    print "alpha: " + str(props.alpha)
    print "beta: " + str(props.beta)
    print "number of keywords: " + str(props.num_keywords)
    print "number of threads: " + str(props.num_thread)
    print "iteration: " + str(props.iter)
    print "seed: " + str(props.seed)


if __name__ == '__main__':
    main()
