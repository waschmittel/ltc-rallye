# LTC Rallye

## Helpful sql scripts

```postgresql
-- list of runners
select name,
       number_of_laps_run,
       bonus_laps,
       fastest,
       average
from runner,
     (select min(duration / 1000) fastest, runner_id from lap where duration > 0 group by runner_id) as fastestlap
where fastestlap.runner_id = runner.id;

-- list of sponsors
select sponsor.name,
       street,
       city,
       sponsor.country,
       per_lap_donation,
       one_time_donation,
       runner.name,
       coalesce(runner.number_of_laps_run, 0)                                  laps,
       coalesce(runner.bonus_laps, 0)                                          bonus,
       coalesce(runner.number_of_laps_run, 0) + coalesce(runner.bonus_laps, 0) points,
       total_donation,
       runner.room_number
from sponsor,
     runner
where sponsor.runner_id = runner.id
order by runner.room_number asc, runner.name asc;
```

## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the side/top bar and the main menu). This
  setup uses
  [App Layout](https://vaadin.com/components/vaadin-app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `views` folder in `frontend/` contains the client-side JavaScript views of your application.
- `themes` folder in `frontend/` contains the custom CSS styles.

## Useful links

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Follow the tutorials at [vaadin.com/tutorials](https://vaadin.com/tutorials).
- Watch training videos and get certified at [vaadin.com/learn/training](https://vaadin.com/learn/training).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Search UI components and their usage examples at [vaadin.com/components](https://vaadin.com/components).
- View use case applications that demonstrate Vaadin capabilities
  at [vaadin.com/examples-and-demos](https://vaadin.com/examples-and-demos).
- Discover Vaadin's set of CSS utility classes that enable building any UI without custom CSS in
  the [docs](https://vaadin.com/docs/latest/ds/foundation/utility-classes).
- Find a collection of solutions to common use cases in [Vaadin Cookbook](https://cookbook.vaadin.com/).
- Find Add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join
  our [Discord channel](https://discord.gg/MYFq5RTbBn).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin/platform).
