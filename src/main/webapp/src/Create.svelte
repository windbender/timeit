<script>
	import ButtonComponent from './ButtonComponent.svelte';

   import { guid  } from './guidstore.js'
   import { api_url } from './util'

   var eventURL=api_url + 'api/event'
   let event_name =""
   async function createEvent() {

      var create = {
         name: event_name
      }
      var createStr = JSON.stringify({ ...create})
      console.log("attempting to create a new event "+eventURL,createStr)
      const res = await fetch(eventURL, {
         method: 'POST',
         mode: "cors",
         headers: {
            "Content-Type": "application/json",
         },
         body: createStr
      })
      if(!res.ok) {
         throw new Error(`HTTP error! status: ${response.status}`);
      }
      var status = res.headers.get('Date')
      var location = res.headers.get('Location')
      var back = await res.json()
      guid.update( old => back )
      console.log("event created, guid="+$guid)
      document.location.assign(api_url +"?guid="+$guid)

   }       

</script>

<main>
   <div><input id="event_name" type="text" bind:value={event_name}></div>
<div>
<ButtonComponent on:click={() => createEvent()} symbol='Create' bgColor={"#C4C41f"}></ButtonComponent>
</div>


</main>

<style>
        table {
          margin-left: auto;
          margin-right: auto;
        }
	main {
		text-align: center;
		padding: 3px;
		margin: 0 auto;
	}
	h1 {
                margin-block-start: 3px;
                margin-block-end: 3px;


		color: #ff3e00;
		text-transform: uppercase;
		font-size: 3em;
		font-weight: 300;
	}

	@media (min-width: 640px) {
		main {
			max-width: none;
		}
	}
</style>
