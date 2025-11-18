import React, { useState, useEffect, useRef } from 'react';
import { Shield, Zap, Coins, Cpu } from 'lucide-react';

const FuturisticSlots = () => {
  const [slots, setSlots] = useState(['equipment', 'mount', 'coin']);
  const [isSpinning, setIsSpinning] = useState(false);
  const [particles, setParticles] = useState([]);
  const [matchedDimension, setMatchedDimension] = useState(null);
  const [winCount, setWinCount] = useState({ equipment: 0, mount: 0, coin: 0 });
  const [showCelebration, setShowCelebration] = useState(false);
  const [failCount, setFailCount] = useState(0);
  const [totalSpins, setTotalSpins] = useState(0);
  const audioContextRef = useRef(null);

  useEffect(() => {
    audioContextRef.current = new (window.AudioContext || window.webkitAudioContext)();
  }, []);

  const playSound = (type) => {
    const ctx = audioContextRef.current;
    if (!ctx) return;

    const oscillator = ctx.createOscillator();
    const gainNode = ctx.createGain();
    
    oscillator.connect(gainNode);
    gainNode.connect(ctx.destination);

    switch(type) {
      case 'equipment':
        oscillator.type = 'sawtooth';
        oscillator.frequency.setValueAtTime(200, ctx.currentTime);
        oscillator.frequency.exponentialRampToValueAtTime(100, ctx.currentTime + 0.3);
        gainNode.gain.setValueAtTime(0.3, ctx.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.3);
        break;
      case 'mount':
        oscillator.type = 'sine';
        oscillator.frequency.setValueAtTime(300, ctx.currentTime);
        oscillator.frequency.exponentialRampToValueAtTime(800, ctx.currentTime + 0.4);
        gainNode.gain.setValueAtTime(0.2, ctx.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.4);
        break;
      case 'coin':
        oscillator.type = 'sine';
        oscillator.frequency.setValueAtTime(800, ctx.currentTime);
        oscillator.frequency.exponentialRampToValueAtTime(1200, ctx.currentTime + 0.2);
        gainNode.gain.setValueAtTime(0.25, ctx.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.2);
        break;
      case 'spin':
        oscillator.type = 'square';
        oscillator.frequency.setValueAtTime(300, ctx.currentTime);
        gainNode.gain.setValueAtTime(0.08, ctx.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.1);
        break;
      case 'win':
        for (let i = 0; i < 3; i++) {
          setTimeout(() => {
            const osc = ctx.createOscillator();
            const gain = ctx.createGain();
            osc.connect(gain);
            gain.connect(ctx.destination);
            osc.frequency.value = 500 + i * 200;
            gain.gain.setValueAtTime(0.2, ctx.currentTime);
            gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.3);
            osc.start();
            osc.stop(ctx.currentTime + 0.3);
          }, i * 100);
        }
        return;
      case 'surprise':
        for (let i = 0; i < 5; i++) {
          setTimeout(() => {
            const osc = ctx.createOscillator();
            const gain = ctx.createGain();
            osc.connect(gain);
            gain.connect(ctx.destination);
            osc.frequency.value = 400 + Math.random() * 600;
            gain.gain.setValueAtTime(0.12, ctx.currentTime);
            gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.25);
            osc.start();
            osc.stop(ctx.currentTime + 0.25);
          }, i * 80);
        }
        return;
    }

    oscillator.start();
    oscillator.stop(ctx.currentTime + 1);
  };

  const dimensions = [
    { 
      id: 'equipment', 
      name: 'Equipment', 
      subtitle: 'Gear & Armor',
      icon: Shield, 
      neon: '#FF9500',
      particle: 'bg-orange-400'
    },
    { 
      id: 'mount', 
      name: 'Mount', 
      subtitle: 'Ride & Speed',
      icon: Zap, 
      neon: '#007AFF',
      particle: 'bg-blue-400'
    },
    { 
      id: 'coin', 
      name: 'Coin', 
      subtitle: 'Gold & Wealth',
      icon: Coins, 
      neon: '#FFD700',
      particle: 'bg-yellow-400'
    }
  ];

  const generateParticles = (type, count = 60) => {
    const newParticles = [];
    const centerX = 50;
    const centerY = 50;
    
    for (let i = 0; i < count; i++) {
      let particle = {
        id: Date.now() + Math.random(),
        type,
        life: 120
      };

      if (type === 'equipment') {
        const angle = (Math.PI * 2 * i) / count;
        particle.x = centerX;
        particle.y = centerY;
        particle.vx = Math.cos(angle) * (3 + Math.random() * 2);
        particle.vy = Math.sin(angle) * (3 + Math.random() * 2);
        particle.size = 3 + Math.random() * 2;
        particle.shape = 'square';
        particle.rotation = Math.random() * 360;
      } else if (type === 'mount') {
        particle.x = -10 + Math.random() * 10;
        particle.y = 20 + Math.random() * 60;
        particle.vx = 6 + Math.random() * 4;
        particle.vy = (Math.random() - 0.5) * 1;
        particle.size = 2 + Math.random() * 2;
        particle.shape = 'trail';
      } else if (type === 'coin') {
        particle.x = centerX + (Math.random() - 0.5) * 40;
        particle.y = -10;
        particle.vx = (Math.random() - 0.5) * 2;
        particle.vy = 2 + Math.random() * 2;
        particle.size = 3 + Math.random() * 2;
        particle.shape = 'circle';
        particle.rotation = 0;
        particle.rotationSpeed = (Math.random() - 0.5) * 20;
      } else if (type === 'surprise') {
        const effectType = i % 3;
        if (effectType === 0) {
          const angle = (Math.PI * 2 * i) / count;
          particle.x = centerX;
          particle.y = centerY;
          particle.vx = Math.cos(angle) * 4;
          particle.vy = Math.sin(angle) * 4;
          particle.size = 3 + Math.random() * 2;
          particle.shape = 'square';
          particle.rotation = Math.random() * 360;
        } else if (effectType === 1) {
          particle.x = Math.random() * 100;
          particle.y = Math.random() * 100;
          particle.vx = (Math.random() - 0.5) * 6;
          particle.vy = (Math.random() - 0.5) * 6;
          particle.size = 2 + Math.random() * 2;
          particle.shape = 'trail';
        } else {
          particle.x = centerX + (Math.random() - 0.5) * 30;
          particle.y = centerY + (Math.random() - 0.5) * 30;
          particle.vx = (Math.random() - 0.5) * 3;
          particle.vy = (Math.random() - 0.5) * 3;
          particle.size = 3 + Math.random() * 2;
          particle.shape = 'circle';
          particle.rotation = 0;
          particle.rotationSpeed = (Math.random() - 0.5) * 20;
        }
      }
      
      newParticles.push(particle);
    }
    setParticles(prev => [...prev, ...newParticles]);
  };

  useEffect(() => {
    if (particles.length === 0) return;
    
    const interval = setInterval(() => {
      setParticles(prev => 
        prev
          .map(p => {
            let newP = { ...p };
            
            if (p.shape === 'square') {
              newP.x = p.x + p.vx;
              newP.y = p.y + p.vy;
              newP.vx = p.vx * 0.98;
              newP.vy = p.vy * 0.98;
              newP.rotation = (p.rotation || 0) + 10;
            } else if (p.shape === 'trail') {
              newP.x = p.x + p.vx;
              newP.y = p.y + p.vy;
              newP.vx = p.vx * 1.05;
            } else if (p.shape === 'circle') {
              newP.x = p.x + p.vx;
              newP.y = p.y + p.vy;
              newP.vy = p.vy + 0.15;
              newP.rotation = (p.rotation || 0) + (p.rotationSpeed || 0);
            } else {
              newP.x = p.x + p.vx;
              newP.y = p.y + p.vy;
            }
            
            newP.life = p.life - 2;
            return newP;
          })
          .filter(p => p.life > 0 && p.x >= -10 && p.x <= 110 && p.y >= -10 && p.y <= 110)
      );
    }, 50);

    return () => clearInterval(interval);
  }, [particles.length]);

  const handleSpin = () => {
    if (isSpinning) return;
    
    setIsSpinning(true);
    setMatchedDimension(null);
    playSound('spin');
    setTotalSpins(prev => prev + 1);
    
    const dimensionTypes = ['equipment', 'mount', 'coin'];
    let spinCount = 0;
    const maxSpins = 20;
    
    const spinInterval = setInterval(() => {
      setSlots([
        dimensionTypes[Math.floor(Math.random() * 3)],
        dimensionTypes[Math.floor(Math.random() * 3)],
        dimensionTypes[Math.floor(Math.random() * 3)]
      ]);
      
      spinCount++;
      
      if (spinCount >= maxSpins) {
        clearInterval(spinInterval);
        
        let finalSlots;
        
        if (failCount >= 4) {
          const minWins = Math.min(winCount.equipment, winCount.mount, winCount.coin);
          const availableDims = dimensionTypes.filter(dim => winCount[dim] === minWins);
          const guaranteedDim = availableDims[Math.floor(Math.random() * availableDims.length)];
          finalSlots = [guaranteedDim, guaranteedDim, guaranteedDim];
          setFailCount(0);
        } else {
          const shouldWin = Math.random() < 0.3;
          
          if (shouldWin) {
            const randomDim = dimensionTypes[Math.floor(Math.random() * 3)];
            finalSlots = [randomDim, randomDim, randomDim];
            setFailCount(0);
          } else {
            finalSlots = [
              dimensionTypes[Math.floor(Math.random() * 3)],
              dimensionTypes[Math.floor(Math.random() * 3)],
              dimensionTypes[Math.floor(Math.random() * 3)]
            ];
            if (finalSlots[0] === finalSlots[1] && finalSlots[1] === finalSlots[2]) {
              finalSlots[2] = dimensionTypes.find(d => d !== finalSlots[0]);
            }
          }
        }
        
        setSlots(finalSlots);
        
        if (finalSlots[0] === finalSlots[1] && finalSlots[1] === finalSlots[2]) {
          const matched = finalSlots[0];
          setMatchedDimension(matched);
          setFailCount(0);
          
          setTimeout(() => {
            playSound('win');
            playSound(matched);
            generateParticles(matched, 80);
            
            setWinCount(prev => ({
              ...prev,
              [matched]: prev[matched] + 1
            }));
            
            const newWinCount = { ...winCount, [matched]: winCount[matched] + 1 };
            if (newWinCount.equipment > 0 && newWinCount.mount > 0 && newWinCount.coin > 0) {
              setTimeout(() => {
                setShowCelebration(true);
                playSound('surprise');
                generateParticles('surprise', 100);
                setTimeout(() => setShowCelebration(false), 3000);
              }, 1000);
            }
          }, 500);
        } else {
          setFailCount(prev => prev + 1);
        }
        
        setIsSpinning(false);
      }
    }, 100);
  };

  return (
    <div className="relative w-full h-screen overflow-hidden" style={{
      background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)'
    }}>
      {/* 动态网格背景 */}
      <div className="absolute inset-0 opacity-20" style={{
        backgroundImage: `
          linear-gradient(rgba(255, 255, 255, 0.3) 1px, transparent 1px),
          linear-gradient(90deg, rgba(255, 255, 255, 0.3) 1px, transparent 1px)
        `,
        backgroundSize: '60px 60px'
      }}></div>

      {/* 浮动光球 */}
      <div className="absolute top-20 left-20 w-96 h-96 rounded-full opacity-30 blur-3xl animate-pulse" style={{
        background: matchedDimension === 'equipment' 
          ? 'radial-gradient(circle, #FF9500, transparent)' 
          : matchedDimension === 'mount'
          ? 'radial-gradient(circle, #007AFF, transparent)'
          : matchedDimension === 'coin'
          ? 'radial-gradient(circle, #FFD700, transparent)'
          : 'radial-gradient(circle, #007AFF, transparent)'
      }}></div>
      
      <div className="absolute bottom-20 right-20 w-80 h-80 rounded-full opacity-20 blur-3xl" style={{
        background: 'radial-gradient(circle, #AF52DE, transparent)'
      }}></div>

      {/* 粒子效果 */}
      <div className="absolute inset-0 pointer-events-none">
        {particles.map(p => {
          const dim = dimensions.find(d => d.id === p.type) || dimensions[0];
          
          if (p.shape === 'square') {
            return (
              <div
                key={p.id}
                className={`absolute ${p.type === 'surprise' ? 'bg-gradient-to-br from-orange-400 via-blue-400 to-yellow-400' : dim.particle}`}
                style={{
                  left: `${p.x}%`,
                  top: `${p.y}%`,
                  width: `${p.size * 2}px`,
                  height: `${p.size * 2}px`,
                  opacity: (p.life / 120) * 0.8,
                  transform: `scale(${p.life / 120}) rotate(${p.rotation}deg)`,
                  filter: 'blur(0.5px)',
                  boxShadow: `0 0 20px ${dim.neon}60`
                }}
              ></div>
            );
          } else if (p.shape === 'trail') {
            return (
              <div
                key={p.id}
                className={`absolute rounded-full ${p.type === 'surprise' ? 'bg-gradient-to-r from-orange-400 via-blue-400 to-yellow-400' : dim.particle}`}
                style={{
                  left: `${p.x}%`,
                  top: `${p.y}%`,
                  width: `${p.size * 8}px`,
                  height: `${p.size * 1.5}px`,
                  opacity: (p.life / 120) * 0.7,
                  transform: `scale(${p.life / 120})`,
                  filter: 'blur(1px)',
                  boxShadow: `0 0 25px ${dim.neon}80`
                }}
              ></div>
            );
          } else {
            return (
              <div
                key={p.id}
                className={`absolute rounded-full ${p.type === 'surprise' ? 'bg-gradient-to-br from-orange-400 via-blue-400 to-yellow-400' : dim.particle}`}
                style={{
                  left: `${p.x}%`,
                  top: `${p.y}%`,
                  width: `${p.size * 2}px`,
                  height: `${p.size * 2}px`,
                  opacity: (p.life / 120) * 0.9,
                  transform: `scale(${p.life / 120}) rotateY(${p.rotation}deg)`,
                  filter: 'blur(0.5px)',
                  boxShadow: `0 0 20px ${dim.neon}70`
                }}
              ></div>
            );
          }
        })}
      </div>

      {/* 庆祝效果 */}
      {showCelebration && (
        <div className="absolute inset-0 flex items-center justify-center z-50 pointer-events-none">
          <div className="text-6xl font-light tracking-tight animate-pulse bg-gradient-to-r from-orange-500 via-blue-500 to-yellow-500 bg-clip-text text-transparent">
            System Unlocked
          </div>
        </div>
      )}

      {/* 主内容 */}
      <div className="relative z-10 flex items-center justify-center h-full px-8 gap-16">
        {/* 老虎机显示 */}
        <div className="relative" style={{
          background: 'rgba(255, 255, 255, 0.25)',
          backdropFilter: 'blur(40px)',
          border: '1px solid rgba(255, 255, 255, 0.4)',
          borderRadius: '32px',
          padding: '48px',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.1), inset 0 1px 0 0 rgba(255, 255, 255, 0.6)'
        }}>
          <div className="absolute top-0 left-1/2 transform -translate-x-1/2 w-32 h-1 rounded-full" style={{
            background: 'linear-gradient(90deg, transparent, rgba(255,255,255,0.6), transparent)'
          }}></div>
          
          <div className="text-gray-500 text-center mb-10 font-light tracking-widest text-xs uppercase">
            Neural Quantum Slots
          </div>
          
          <div className="flex gap-8">
            {slots.map((slotType, index) => {
              const dim = dimensions.find(d => d.id === slotType);
              const Icon = dim.icon;
              
              return (
                <div
                  key={index}
                  className="relative group"
                  style={{
                    width: '160px',
                    height: '200px'
                  }}
                >
                  <div className="absolute inset-0 rounded-3xl transition-all duration-500" style={{
                    background: isSpinning 
                      ? 'rgba(255, 255, 255, 0.15)' 
                      : 'rgba(255, 255, 255, 0.3)',
                    backdropFilter: 'blur(20px)',
                    border: `1px solid ${isSpinning ? 'rgba(255,255,255,0.2)' : `${dim.neon}40`}`,
                    boxShadow: isSpinning 
                      ? '0 8px 32px rgba(0,0,0,0.08)' 
                      : `0 8px 32px rgba(0,0,0,0.08), 0 0 0 1px ${dim.neon}20, inset 0 1px 0 rgba(255,255,255,0.5)`
                  }}></div>
                  
                  {!isSpinning && (
                    <div className="absolute inset-0 rounded-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-500" style={{
                      background: `radial-gradient(circle at 50% 0%, ${dim.neon}15, transparent 70%)`,
                      pointerEvents: 'none'
                    }}></div>
                  )}
                  
                  <div className="relative h-full flex flex-col items-center justify-center gap-4 p-6">
                    <div className="relative">
                      <div className="absolute inset-0 blur-xl opacity-50" style={{
                        background: dim.neon
                      }}></div>
                      <Icon size={64} style={{
                        color: dim.neon,
                        strokeWidth: 1.2,
                        filter: 'drop-shadow(0 4px 12px rgba(0,0,0,0.1))'
                      }} />
                    </div>
                    <div className="text-gray-800 font-light text-xl tracking-tight">
                      {dim.name}
                    </div>
                    <div className="text-xs text-gray-400 font-light tracking-wider uppercase">
                      {dim.subtitle}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* 控制面板 */}
        <div className="flex flex-col gap-8" style={{ width: '440px' }}>
          <div className="relative" style={{
            background: 'rgba(255, 255, 255, 0.25)',
            backdropFilter: 'blur(40px)',
            border: '1px solid rgba(255, 255, 255, 0.4)',
            borderRadius: '32px',
            padding: '32px',
            boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.1), inset 0 1px 0 0 rgba(255, 255, 255, 0.6)'
          }}>
            <div className="text-gray-500 text-center mb-8 font-light tracking-widest text-xs uppercase">
              System Status
            </div>
            
            <div className="space-y-4">
              {dimensions.map(dim => {
                const Icon = dim.icon;
                const count = winCount[dim.id];
                
                return (
                  <div key={dim.id} className="flex items-center justify-between group">
                    <div className="flex items-center gap-4">
                      <div className="relative w-12 h-12 rounded-2xl flex items-center justify-center transition-all duration-300" style={{
                        background: count > 0 
                          ? `linear-gradient(135deg, ${dim.neon}20, ${dim.neon}10)` 
                          : 'rgba(255, 255, 255, 0.2)',
                        border: `1px solid ${count > 0 ? `${dim.neon}30` : 'rgba(255,255,255,0.3)'}`,
                        boxShadow: count > 0 ? `0 4px 16px ${dim.neon}30` : 'none'
                      }}>
                        <Icon size={22} style={{ 
                          color: count > 0 ? dim.neon : '#A0A0A0',
                          strokeWidth: 1.2
                        }} />
                      </div>
                      <span className="text-gray-700 font-light text-base tracking-tight">{dim.name}</span>
                    </div>
                    <div className="flex items-center gap-4">
                      <div className="px-4 py-1.5 text-xs font-light rounded-full tracking-wide transition-all duration-300" style={{
                        background: count > 0 
                          ? `linear-gradient(135deg, ${dim.neon}, ${dim.neon}dd)` 
                          : 'rgba(200, 200, 200, 0.3)',
                        color: count > 0 ? '#fff' : '#888',
                        boxShadow: count > 0 ? `0 4px 12px ${dim.neon}40` : 'none'
                      }}>
                        {count > 0 ? 'Online' : 'Offline'}
                      </div>
                      <span className="text-gray-400 text-sm font-light w-10 text-right">×{count}</span>
                    </div>
                  </div>
                );
              })}
            </div>
            
            {winCount.equipment > 0 && winCount.mount > 0 && winCount.coin > 0 && (
              <div className="mt-8 pt-6 border-t border-white/20 text-center font-light text-sm tracking-wide bg-gradient-to-r from-orange-500 via-blue-500 to-yellow-500 bg-clip-text text-transparent">
                ✦ Full System Operational ✦
              </div>
            )}
            
            <div className="mt-8 pt-6 border-t border-white/20 flex justify-between text-xs font-light text-gray-500">
              <span>Total Spins <span className="text-gray-700 ml-2">{totalSpins}</span></span>
              {failCount > 0 && (
                <span>Boost <span className="text-orange-500 ml-2">{failCount}/4</span></span>
              )}
            </div>
          </div>

          <div className="space-y-4">
            {matchedDimension && (
              <div className="relative text-center py-5 rounded-2xl transition-all duration-500" style={{
                background: 'rgba(255, 255, 255, 0.3)',
                backdropFilter: 'blur(20px)',
                border: `1px solid ${dimensions.find(d => d.id === matchedDimension)?.neon}50`,
                boxShadow: `0 8px 32px ${dimensions.find(d => d.id === matchedDimension)?.neon}20`
              }}>
                <div className="font-light text-lg tracking-tight" style={{
                  color: dimensions.find(d => d.id === matchedDimension)?.neon
                }}>
                  {dimensions.find(d => d.id === matchedDimension)?.name} Acquired
                </div>
              </div>
            )}
            
            {failCount >= 3 && !matchedDimension && (
              <div className="relative text-center py-5 rounded-2xl" style={{
                background: 'rgba(255, 149, 0, 0.1)',
                backdropFilter: 'blur(20px)',
                border: '1px solid rgba(255, 149, 0, 0.3)',
                boxShadow: '0 8px 32px rgba(255, 149, 0, 0.2)'
              }}>
                <div className="font-light text-sm text-orange-600 tracking-wide">
                  Fortune Boost Activated
                </div>
              </div>
            )}

            <button
              onClick={handleSpin}
              disabled={isSpinning}
              className="w-full rounded-2xl transition-all duration-500 flex items-center justify-center gap-4 group relative overflow-hidden"
              style={{
                background: isSpinning 
                  ? 'rgba(200, 200, 200, 0.3)' 
                  : 'linear-gradient(135deg, rgba(0, 122, 255, 0.9), rgba(88, 86, 214, 0.9))',
                backdropFilter: 'blur(20px)',
                color: '#fff',
                boxShadow: isSpinning 
                  ? 'none' 
                  : '0 20px 40px rgba(0, 122, 255, 0.3), inset 0 1px 0 rgba(255,255,255,0.3)',
                padding: '24px',
                cursor: isSpinning ? 'not-allowed' : 'pointer',
                border: '1px solid rgba(255,255,255,0.2)'
              }}
            >
              <Cpu size={28} className={isSpinning ? 'animate-spin' : ''} style={{ strokeWidth: 1.2 }} />
              <span className="font-light text-xl tracking-tight">
                {isSpinning ? 'Processing' : 'Initialize'}
              </span>
              {!isSpinning && (
                <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white to-transparent opacity-20 transform -translate-x-full group-hover:translate-x-full transition-transform duration-1000"></div>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FuturisticSlots