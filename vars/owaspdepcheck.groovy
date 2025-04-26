def call(Map args = [:]) {
    def additionalArguments = args.get('additionalArguments', '--scan ./')
    def odcInstallation = args.get('odcInstallation', 'owasp')
    def reportPattern = args.get('reportPattern', '**/dependency-check-report.xml')
    
    // Execute the Dependency Check scan
    dependencyCheck additionalArguments: additionalArguments, odcInstallation: odcInstallation
    
    // Publish the Dependency Check report
    dependencyCheckPublisher pattern: reportPattern
}
