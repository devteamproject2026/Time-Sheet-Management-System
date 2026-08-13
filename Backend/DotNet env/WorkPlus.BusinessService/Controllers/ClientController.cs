using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WorkPlus.BusinessService.DTOs;
using WorkPlus.BusinessService.Services;

namespace WorkPlus.BusinessService.Controllers
{
    [ApiController]
    [Route("api/business/clients")]
    [Authorize]
    public class ClientController : ControllerBase
    {
        private readonly IBusinessService _businessService;

        public ClientController(IBusinessService businessService)
        {
            _businessService = businessService;
        }

        [Authorize(Roles = "HR_HEAD,ADMIN")]
        [HttpPost]
        public async Task<IActionResult> CreateClient([FromBody] ClientRequest request)
        {
            var client = await _businessService.CreateClientAsync(request);
            return CreatedAtAction(nameof(GetClientById), new { id = client.ClientId }, client);
        }

        [HttpGet]
        public async Task<IActionResult> GetAllClients()
        {
            var clients = await _businessService.GetAllClientsAsync();
            return Ok(clients);
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetClientById(int id)
        {
            var client = await _businessService.GetClientByIdAsync(id);
            return client != null ? Ok(client) : NotFound();
        }

        [Authorize(Roles = "HR_HEAD,ADMIN")]
        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateClient(int id, [FromBody] ClientRequest request)
        {
            var updated = await _businessService.UpdateClientAsync(id, request);
            return Ok(updated);
        }

        [Authorize(Roles = "HR_HEAD,ADMIN")]
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteClient(int id)
        {
            await _businessService.DeleteClientAsync(id);
            return NoContent();
        }
    }
}
