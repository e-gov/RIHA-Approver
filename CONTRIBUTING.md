# Introduction

This repository is one of the components of the RIHA infrastructure and changes in RIHA-Approver may trigger the need of changes in RIHA-Browser as well. RIHA-Approver's main function is to act as an API between RIHA-Browser and RIHA-Storage components.


## Prerequisits of submitting a change in RIHA-Approver

- Your code is reasonably covered with unit tests
- Your code has been tested
- RIHA-Approver's API description (swagger.yaml) has been updated

Updating the API description is automated and should be regenerated before each merge/pull request. API descriptions are generated using a [swagger maven plugin](https://github.com/kongchen/swagger-maven-plugin) which uses annotations in Java code to generate a new desciption. To run the generation, use 
 `mvn swagger:generate` which will generate a swagger.yaml file in `src/main/resources/static` folder.
