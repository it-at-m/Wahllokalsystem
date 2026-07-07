# Beziehungen der Workflows und Actions

Unsere Workflows bestehen aus wiederverwendbaren Workflows aus unserem Repository, sowie aus Actions von ITM
und anderen Github-Actions.

## Legende

```mermaid
flowchart LR
    idTriggerable([Ein Workflow, der über einen automatischen Trigger gestartet wird])
    idDispatchable>Ein manuell startbarer Workflow]
    idReusable[[Ein wiederverwendbarer Workflow]]
    idGithubAction[Eine Github-Action]
```

<em>Legende zu den verwendeten Symbolen</em>

### Pull-Requests nach Dev

```mermaid
flowchart LR
    subgraph wls
        anyPR([jeder PR])
    end

    subgraph githubActions
       labeler 
       auto-assign
       populate-release-pr-links
    end
    anyPR --> labeler
    anyPR --> auto-assign
    anyPR --> populate-release-pr-links
    populate-release-pr-links --> note[/Nur wenn im PR Namen "Release" vorkommt/] 
    
    classDef text fill:#ffffff,font-style:italic,stroke:#4f4f4f
    class note text
```

<em>Workflow und Action, die bei jedem Pull-Request ausgeführt wird</em>

```mermaid
flowchart LR
    subgraph wls
        backendServicePR([Backend-Service PR])
        wlsCommonPR([Wls-common PR])
        mvnVerify[[callable-run-mvn-verify]]
    end

    subgraph githubActions
        checkout
        setup-java
    end

    backendServicePR --> mvnVerify
    wlsCommonPR --> mvnVerify
    mvnVerify --> checkout
    mvnVerify --> setup-java
```

<em>Workflows und Actions, die bei einem Pull-Requests eines Backend-Services oder bei Wls-common ausgeführt werden</em>

```mermaid
flowchart LR
    subgraph wls
        frontendPR([Frontend PR])
        npmBuild[[callable-run-npm-build]]
    end

    subgraph githubActions
        checkout
        setup-node
    end

    frontendPR --> npmBuild
    npmBuild --> checkout
    npmBuild --> setup-node
```

<em>Workflows und Actions, die bei einem Pull-Request zu einem Frontend ausgeführt werden</em>

```mermaid
flowchart LR
    subgraph wls
        docsPR([Docs PR])
    end

    subgraph itmAction
        action-build-docs
    end

    docsPR --> action-build-docs
```

<em>Workflow und Action, die bei einem Pull-Request zur Dokumentation ausgeführt werden</em>

### Push auf Dev

```mermaid
flowchart LR
    subgraph wls
        backendServiceDevPush([Backend-Service Push])

        buildImage[[callable-create-github-container-image]]
    end

    subgraph githubActions
        checkout
        setup-java
        docker/login-action
        docker/metadata-action
        docker/build-push-action
    end

    backendServiceDevPush --> buildImage
    buildImage --> checkout
    buildImage --> setup-java
    buildImage --> docker/login-action
    buildImage --> docker/metadata-action
    buildImage --> docker/build-push-action
```

<em>Workflows und Actions, die bei einem Push eines Backend-Services ausgeführt werden</em>

```mermaid
flowchart LR
    subgraph wls
        frontendDevPush([Frontend Push])

        buildImageFrontend[[callable-create-github-container-image-frontend]]
    end

    subgraph githubActions
        checkout
        setup-node
        docker/login-action
        docker/metadata-action
        docker/build-push-action
    end

    frontendDevPush --> buildImageFrontend
    buildImageFrontend --> checkout
    buildImageFrontend --> setup-node
    buildImageFrontend --> docker/login-action
    buildImageFrontend --> docker/metadata-action
    buildImageFrontend --> docker/build-push-action
```

<em>Workflows und Actions, die bei einem Push eines Frontends ausgeführt werden</em>

```mermaid
flowchart LR
    subgraph wls
        docsDevPush([Docs Push])
    end

    subgraph githubActions
        checkout
        setup-node
        peaceiris/actions-gh-pages
    end

    docsDevPush --> checkout
    docsDevPush --> setup-node
    docsDevPush --> peaceiris/actions-gh-pages
```

<em>Workflow und Actions, die bei einem Push der Dokumentation ausgeführt werden</em>

### Manuell gestartet

```mermaid
flowchart LR
    subgraph wls
        dispatchBuildImage>dispatch-create-github-container-image]

        buildImage[[callable-create-github-container-image]]
    end

    subgraph githubActions
        checkout
        setup-java
        docker/login-action
        docker/metadata-action
        docker/build-push-action
    end

    dispatchBuildImage --> buildImage
    buildImage --> checkout
    buildImage --> setup-java
    buildImage --> docker/login-action
    buildImage --> docker/metadata-action
    buildImage --> docker/build-push-action
```

<em>Workflows und Actions, die bei der Erstellung eines Images für einen Backend-Service ausgeführt werden</em>

```mermaid
flowchart LR
    subgraph wls
        dispatchBuildImageFrontend>dispatch-create-github-container-image-frontend]

        buildImageFrontend[[callable-create-github-container-image-frontend]]
    end

    subgraph githubActions
        checkout
        setup-node
        docker/login-action
        docker/metadata-action
        docker/build-push-action
    end

    dispatchBuildImageFrontend --> buildImageFrontend
    buildImageFrontend --> checkout
    buildImageFrontend --> setup-node
    buildImageFrontend --> docker/login-action
    buildImageFrontend --> docker/metadata-action
    buildImageFrontend --> docker/build-push-action
```

<em>Workflows und Actions, die bei der Erstellung eines Images für ein Frontend ausgeführt werden</em>

```mermaid
flowchart LR
    subgraph wls
        dispatchMicroserviceMvnRelease>dispatch-microservice-mvn-release]

        buildGHRelease[[callable-create-github-release-from-tag]]
        buildImage[[callable-create-github-container-image]]
        
    end

    subgraph githubActions
        checkout
        setup-java
        docker/login-action
        docker/metadata-action
        docker/build-push-action
        fregante/setup-git-user
        softprops/action-gh-release
    end

    dispatchMicroserviceMvnRelease --> checkout
    dispatchMicroserviceMvnRelease --> fregante/setup-git-user
    dispatchMicroserviceMvnRelease --> setup-java
    dispatchMicroserviceMvnRelease --> buildGHRelease
    dispatchMicroserviceMvnRelease --> buildImage
    buildImage --> checkout
    buildImage --> setup-java
    buildImage --> docker/login-action
    buildImage --> docker/metadata-action
    buildImage --> docker/build-push-action
    buildGHRelease --> checkout
    buildGHRelease --> setup-java
    buildGHRelease --> softprops/action-gh-release
```

<em>Workflows und Actions, die bei der Durchführung eines Releases eines Backend-Services ausgeführt werden</em>

```mermaid
flowchart LR
    subgraph wls
        dispatchWlsCommonMvnRelease>create wls-common release]
    end
    
    subgraph githubActions
        checkout
        fregante/setup-git-user
        setup-java
        softprops/action-gh-release
    end

    dispatchWlsCommonMvnRelease --> checkout
    dispatchWlsCommonMvnRelease --> fregante/setup-git-user
    dispatchWlsCommonMvnRelease --> setup-java
    dispatchWlsCommonMvnRelease --> softprops/action-gh-release

```

<em>Workflow und Actions, die bei der Durchführung eines Releases für Wls-common ausgeführt werden</em>
