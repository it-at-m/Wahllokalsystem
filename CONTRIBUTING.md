# Contributing

Thank you for contributing! To keep reviews efficient and minimize the number of review cycles, please follow the quality gates and pre-merge checklist below.

## How to Contribute

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please open an issue with the tag "enhancement", fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Open an issue with the tag "enhancement"
2. Fork the Project
3. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
4. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
5. Push to the Branch (`git push origin feature/AmazingFeature`)
6. Open a Pull Request

More about this in the [CODE_OF_CONDUCT](/CODE_OF_CONDUCT.md) file.

## Quality gates (what we expect before opening a PR)
Before requesting review, ensure your changes satisfy the following gates locally and in CI:

- Code lints cleanly (no new lint errors).
- Automatic lint fixes applied where possible.
- Backend and frontend unit/integration tests pass.
- Project builds successfully (frontend, backend, docs as applicable).
- Follow repository code style and commit message conventions.

Running these checks locally reduces review iterations and speeds up merges.

## Required local commands

Frontend
- Check linting:
  ```bash
  npm run lint
  ```
- Auto-fix lintable issues:
  ```bash
  npm run fix
- Test the frontend :
  ```bash
  npm run test
  ```
- Build the frontend :
  ```bash
  npm run build
  ```

Backend
- Run the full Maven build, including tests and verification:
  ```bash
  mvn clean verify
  ```

Documentation (Doc)
- Check documentation linting:
  ```bash
  npm run lint
  ```
- Build the documentation site:
  ```bash
  npm run build
  ```

## Pull request checklist
- [ ] All lint checks pass locally (use fix where possible).
- [ ] All tests pass locally (unit and integration where applicable).
- [ ] Frontend/backend/docs build successfully.
- [ ] Relevant documentation updated (if behavior or public API changed).
- [ ] PR description explains the why (not just the what) and includes testing steps.

## CI expectations
CI will run the same quality gates. A PR that fails lint, tests or build will be blocked from merging until fixed. Please treat CI failures as part of the normal feedback loop and address them promptly.


Thank you for helping keep our codebase healthy and maintainable!