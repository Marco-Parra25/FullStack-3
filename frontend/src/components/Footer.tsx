export default function Footer() {
  const centros = [
    'Hospital RedNorte Antofagasta',
    'Centro de Salud Iquique',
    'Clínica Especializada Arica',
  ]

  return (
    <footer className="portal-footer">
      <div className="portal-footer-grid">

        <div>
          <p className="portal-footer-section-title">Atención al paciente</p>
          <p className="portal-footer-line">📞 600 360 7777</p>
          <p className="portal-footer-line">✉ contacto@rednorte.cl</p>
          <p className="portal-footer-muted">
            Para consultas sobre tu lista de espera, derivaciones o resultados de exámenes.
          </p>
        </div>

        <div>
          <p className="portal-footer-section-title">Horarios de atención</p>
          <p className="portal-footer-line">Lunes a viernes: 08:00 – 20:00</p>
          <p className="portal-footer-line">Sábado: 09:00 – 14:00</p>
          <p className="portal-footer-line">Domingo y festivos: cerrado</p>
          <p className="portal-footer-muted">Urgencias disponibles las 24 hrs en todos los centros.</p>
        </div>

        <div>
          <p className="portal-footer-section-title">Centros de salud RedNorte</p>
          {centros.map(centro => (
            <p key={centro} className="portal-footer-center-item">
              <span className="portal-footer-center-name">{centro}</span>
            </p>
          ))}
        </div>

      </div>

      <div className="portal-footer-bottom">
        <p className="portal-footer-legal">
          © {new Date().getFullYear()} RedNorte — Red de Salud del Norte de Chile. Todos los derechos reservados.
        </p>
        <p className="portal-footer-legal">
          FONASA · Superintendencia de Salud Reg. N° 4821
        </p>
      </div>
    </footer>
  )
}
